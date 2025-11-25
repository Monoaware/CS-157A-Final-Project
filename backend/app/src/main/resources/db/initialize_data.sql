-- Initialize sample data for the library database

-- Insert sample users
INSERT INTO users (fname, lname, email, passwordhash, role, status) VALUES
('John', 'Doe', 'john.doe@example.com', '$2a$10$samplehash1', 'MEMBER', 'ACTIVE'),
('Jane', 'Smith', 'jane.smith@example.com', '$2a$10$samplehash2', 'MEMBER', 'ACTIVE'),
('Bob', 'Johnson', 'bob.johnson@example.com', '$2a$10$samplehash3', 'MEMBER', 'ACTIVE'),
('Alice', 'Williams', 'alice.williams@example.com', '$2a$10$samplehash4', 'STAFF', 'ACTIVE');

-- Insert sample titles
INSERT INTO titles (ISBN, title, author, yearPublished, genre, isVisible) VALUES
('978-0-13-110362-7', 'The C Programming Language', 'Brian W. Kernighan', 1988, 'Computer Science', TRUE),
('978-0-201-61622-4', 'The Pragmatic Programmer', 'David Thomas', 1999, 'Computer Science', TRUE),
('978-0-201-63361-0', 'Design Patterns', 'Gang of Four', 1994, 'Computer Science', TRUE),
('978-0-262-03384-8', 'Introduction to Algorithms', 'Thomas H. Cormen', 2009, 'Computer Science', TRUE),
('978-0-14-028329-7', 'To Kill a Mockingbird', 'Harper Lee', 1960, 'Fiction', TRUE);

-- Insert sample copies
INSERT INTO copies (titleid, barcode, status, location, isvisible) VALUES
(1, 'COPY001', 'AVAILABLE', 'Shelf A1', TRUE),
(1, 'COPY002', 'CHECKED_OUT', 'Shelf A1', TRUE),
(2, 'COPY003', 'AVAILABLE', 'Shelf B2', TRUE),
(3, 'COPY004', 'AVAILABLE', 'Shelf C3', TRUE),
(4, 'COPY005', 'CHECKED_OUT', 'Shelf D4', TRUE),
(5, 'COPY006', 'AVAILABLE', 'Shelf E5', TRUE);

-- Insert sample book records (loans)
INSERT INTO book_records (copyid, userid, checkoutdate, duedate, returndate, renewcount) VALUES
(2, 1, '2025-11-01 10:00:00', '2025-11-15 23:59:59', NULL, 0),
(5, 3, '2025-11-10 14:30:00', '2025-11-24 23:59:59', NULL, 1);

-- Insert sample fines
INSERT INTO fines (userid, loanid, amount, finedate, reason, status) VALUES
(1, 1, 5.50, '2025-11-20 09:00:00', 'Overdue book', 'UNPAID');

-- Insert sample holds
INSERT INTO holds (userid, titleid, copyid, status, placedat, readyat, pickupexpire, position) VALUES
(2, 1, 1, 'READY', '2025-11-15 11:00:00', '2025-11-20 15:00:00', '2025-11-27 23:59:59', 1);
