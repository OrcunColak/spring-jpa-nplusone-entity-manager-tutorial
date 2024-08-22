-- Insert authors
INSERT INTO author (name) VALUES
('Author 1'),
('Author 2'),
('Author 3'),
('Author 4');


-- Insert books
INSERT INTO book (title, author_id) VALUES
('Book 1 by Author 1', 1),
('Book 2 by Author 1', 1),
('Book 3 by Author 2', 2),
('Book 4 by Author 3', 3),
('Book 5 by Author 3', 3);
