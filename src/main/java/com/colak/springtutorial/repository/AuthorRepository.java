package com.colak.springtutorial.repository;

import com.colak.springtutorial.jpa.Author;
import com.colak.springtutorial.jpa.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This repository uses EntityManager API to solve N+1 Select Problem
 */
@Repository
@RequiredArgsConstructor
public class AuthorRepository {

    private final EntityManager entityManager;

    // See https://medium.com/jpa-java-persistence-api-guide/hibernate-lazyinitializationexception-solutions-7b32bfc0ce98
    // Use JPQL + EntityManager
    public Author findByIdWithBooks(@Param("authorId") Long authorId) {
        TypedQuery<Author> query = entityManager.createQuery(
                "SELECT a FROM Author a LEFT JOIN FETCH a.books WHERE a.id = :authorId", Author.class);
        query.setParameter("authorId", authorId);
        return query.getSingleResult();
    }

    // LEFT JOIN
    // Use CriteriaBuilder
    public List<Author> findAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Author> criteriaQuery = criteriaBuilder.createQuery(Author.class);

        // Define the root for the Author entity
        Root<Author> root = criteriaQuery.from(Author.class);

        // Perform a left join with the Book entity
        root.fetch("books", JoinType.LEFT);

        TypedQuery<Author> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }

    // INNER JOIN
    public List<Author> findByIdAndBookTitle(Long authorId, String title) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Author> criteriaQuery = criteriaBuilder.createQuery(Author.class);

        // Define the root for the Author entity
        Root<Author> root = criteriaQuery.from(Author.class);

        // Eager fetch the books association
        Fetch<Author, Book> books = root.fetch("books", JoinType.INNER);  // Eager fetch with INNER JOIN

        // Cast to Join for applying predicates
        Join<Author, Book> bookJoin = (Join<Author, Book>) books;

        Predicate predicate = criteriaBuilder.equal(root.get("id"), authorId);
        Predicate bookTitlePredicate = criteriaBuilder.equal(bookJoin.get("title"), title);

        predicate = criteriaBuilder.and(predicate, bookTitlePredicate);

        criteriaQuery.where(predicate);

        // Ensure distinct results
        criteriaQuery.select(root).distinct(true);

        TypedQuery<Author> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }
}
