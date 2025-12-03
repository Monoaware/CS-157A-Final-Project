-- Initialize sample data for the library database

-- Insert sample users
INSERT INTO users (fname, lname, email, passwordhash, role, status) VALUES
('John', 'Doe', 'john.doe@example.com', '$2a$10$samplehash1', 'MEMBER', 'ACTIVE'),
('Jane', 'Smith', 'jane.smith@example.com', '$2a$10$samplehash2', 'MEMBER', 'ACTIVE'),
('Bob', 'Johnson', 'bob.johnson@example.com', '$2a$10$samplehash3', 'MEMBER', 'ACTIVE'),
('Alice', 'Williams', 'alice.williams@example.com', '$2a$10$samplehash4', 'STAFF', 'ACTIVE'),
('Admin', 'Admin', 'Admin@example.com', 'Admin', 'STAFF', 'ACTIVE'),
('Admin', 'Admin', 'Admin@account', '$2a$12$XGQIl7314bLwkEPat91jVObWlFPxpM59M0JuAqg57nu4hP5nzCZKC', 'STAFF', 'ACTIVE'),
('Member', 'Member', 'Member@account', '$2a$12$7XGrjNemO0CIRlQeiJYSDucEKPqxqSK0n3OltklvSb3R4.h3Vz0nm', 'MEMBER', 'ACTIVE'),
('Alice', 'Thompson', 'alice.thompson@example.com', 'hash_1a92bd0c', 'STAFF', 'ACTIVE'),
('Brian', 'Lopez', 'brian.lopez@example.com', 'hash_3fc82a10', 'STAFF', 'ACTIVE'),
('Chloe', 'Nguyen', 'chloe.nguyen@example.com', 'hash_9c41de77', 'STAFF', 'ACTIVE'),
('Daniel', 'Carter', 'daniel.carter@example.com', 'hash_f01ac8a2', 'STAFF', 'INACTIVE'),
('Erica', 'Hughes', 'erica.hughes@example.com', 'hash_7150be22', 'STAFF', 'ACTIVE'),
('Frank', 'Ramos', 'frank.ramos@example.com', 'hash_33aa1ae1', 'STAFF', 'ACTIVE'),
('Grace', 'Mitchell', 'grace.mitchell@example.com', 'hash_8bb01ffd', 'STAFF', 'ACTIVE'),
('Henry', 'Patel', 'henry.patel@example.com', 'hash_442ffaf0', 'STAFF', 'INACTIVE'),
('Isabella', 'Morales', 'isabella.morales@example.com', 'hash_aa10cc4e', 'STAFF', 'ACTIVE'),
('Jacob', 'Stevens', 'jacob.stevens@example.com', 'hash_91f4bb22', 'STAFF', 'ACTIVE'),
('Mason', 'Cole', 'mason.cole@example.com', 'hash_c411ded0', 'MEMBER', 'ACTIVE'),
('Nora', 'Foster', 'nora.foster@example.com', 'hash_1ee02fa3', 'MEMBER', 'ACTIVE'),
('Owen', 'Price', 'owen.price@example.com', 'hash_9cbb0131', 'MEMBER', 'INACTIVE'),
('Paige', 'Baker', 'paige.baker@example.com', 'hash_511afc82', 'MEMBER', 'ACTIVE'),
('Quinn', 'Jenkins', 'quinn.jenkins@example.com', 'hash_d51b76e9', 'MEMBER', 'ACTIVE'),
('Riley', 'Kim', 'riley.kim@example.com', 'hash_17dfb293', 'MEMBER', 'ACTIVE'),
('Sofia', 'Garcia', 'sofia.garcia@example.com', 'hash_28edce10', 'MEMBER', 'INACTIVE'),
('Tyler', 'Barnes', 'tyler.barnes@example.com', 'hash_b78da391', 'MEMBER', 'ACTIVE'),
('Uma', 'Chowdhury', 'uma.chowdhury@example.com', 'hash_7ae1023f', 'MEMBER', 'ACTIVE'),
('Victor', 'Sanders', 'victor.sanders@example.com', 'hash_11b9ca42', 'MEMBER', 'ACTIVE'),
('Wendy', 'Harrison', 'wendy.harrison@example.com', 'hash_74cf0a12', 'MEMBER', 'INACTIVE'),
('Xavier', 'Stone', 'xavier.stone@example.com', 'hash_88daaa22', 'MEMBER', 'ACTIVE'),
('Yara', 'Valdez', 'yara.valdez@example.com', 'hash_1930aae2', 'MEMBER', 'ACTIVE'),
('Zane', 'Walsh', 'zane.walsh@example.com', 'hash_9f221cb1', 'MEMBER', 'ACTIVE'),
('Aiden', 'Ross', 'aiden.ross@example.com', 'hash_214bf311', 'MEMBER', 'ACTIVE'),
('Brooke', 'Andrews', 'brooke.andrews@example.com', 'hash_5cf304e4', 'MEMBER', 'ACTIVE'),
('Caleb', 'Mendoza', 'caleb.mendoza@example.com', 'hash_f31299be', 'MEMBER', 'INACTIVE'),
('Delilah', 'Park', 'delilah.park@example.com', 'hash_728acc11', 'MEMBER', 'ACTIVE'),
('Ethan', 'Wright', 'ethan.wright@example.com', 'hash_3d2c11fa', 'MEMBER', 'ACTIVE'),
('Faith', 'Bennett', 'faith.bennett@example.com', 'hash_ae100c52', 'MEMBER', 'ACTIVE')
ON CONFLICT (email) DO NOTHING;

-- Insert sample titles
INSERT INTO titles (ISBN, title, author, yearPublished, genre, isVisible) VALUES
('978-0-13-110362-7', 'The C Programming Language', 'Brian W. Kernighan', 1988, 'ART', TRUE),
('978-0-201-61622-4', 'The Pragmatic Programmer', 'David Thomas', 1999, 'ART', TRUE),
('978-0-201-63361-0', 'Design Patterns', 'Gang of Four', 1994, 'ART', TRUE),
('978-0-262-03384-8', 'Introduction to Algorithms', 'Thomas H. Cormen', 2009, 'ART', TRUE),
('978-0-14-028329-7', 'To Kill a Mockingbird', 'Harper Lee', 1960, 'Fiction', TRUE),
('978-0-452-28423-4', '1984', 'George Orwell', 1949, 'Fiction', TRUE),
('978-0-7432-7356-5', 'The Great Gatsby', 'F. Scott Fitzgerald', 1925, 'Fiction', TRUE),
('978-0-316-76948-0', 'The Silent Patient', 'Alex Michaelides', 2019, 'Fiction', TRUE),
('978-0-553-21311-7', 'Dune', 'Frank Herbert', 1965, 'Fiction', TRUE),
('978-1-5011-6877-2', 'The Outsider', 'Stephen King', 2018, 'Fiction', TRUE),
('978-0-375-50420-1', 'The Road', 'Cormac McCarthy', 2006, 'Fiction', TRUE),
('978-0-7432-7357-2', 'The Catcher in the Rye', 'J.D. Salinger', 1951, 'Fiction', TRUE),
('978-1-250-30694-6', 'Project Hail Mary', 'Andy Weir', 2021, 'Fiction', TRUE),
('978-0-06-231611-0', 'The Alchemist', 'Paulo Coelho', 1988, 'Fiction', TRUE),
('978-0-7851-8460-7', 'Civil War', 'Mark Millar', 2007, 'Fiction', TRUE),
('978-0-00-744803-6', 'A Game of Thrones', 'George R.R. Martin', 1996, 'Fantasy', TRUE),
('978-1-101-90327-1', 'Ready Player One', 'Ernest Cline', 2011, 'Fiction', TRUE),
('978-1-59420-009-9', 'The Kite Runner', 'Khaled Hosseini', 2003, 'Fiction', TRUE),
('978-0-374-52903-3', 'All the Light We Cannot See', 'Anthony Doerr', 2014, 'Fiction', TRUE),
('978-0-374-53244-6', 'Cloud Atlas', 'David Mitchell', 2004, 'Fiction', TRUE)
ON CONFLICT (isbn) DO NOTHING;

-- Insert sample copies
INSERT INTO copies (titleid, barcode, status, location, isvisible) VALUES
(1, 'COPY001', 'AVAILABLE', 'Shelf A1', TRUE),
(1, 'COPY002', 'CHECKED_OUT', 'Shelf A1', TRUE),
(2, 'COPY003', 'AVAILABLE', 'Shelf B2', TRUE),
(3, 'COPY004', 'AVAILABLE', 'Shelf C3', TRUE),
(4, 'COPY005', 'CHECKED_OUT', 'Shelf D4', TRUE),
(5, 'COPY006', 'AVAILABLE', 'Shelf E5', TRUE),
(1, 'COPY007', 'AVAILABLE', 'Shelf A2', TRUE),
(1, 'COPY008', 'LOST', 'Shelf A2', TRUE),
(2, 'COPY009', 'AVAILABLE', 'Shelf B1', TRUE),
(2, 'COPY010', 'CHECKED_OUT', 'Shelf B1', TRUE),
(3, 'COPY011', 'AVAILABLE', 'Shelf C1', TRUE),
(3, 'COPY012', 'DAMAGED', 'Shelf C1', TRUE),
(4, 'COPY013', 'AVAILABLE', 'Shelf D1', TRUE),
(4, 'COPY014', 'CHECKED_OUT', 'Shelf D1', TRUE),
(5, 'COPY015', 'AVAILABLE', 'Shelf E1', TRUE),
(5, 'COPY016', 'AVAILABLE', 'Shelf E1', TRUE),
(6, 'COPY017', 'AVAILABLE', 'Shelf F1', TRUE),
(6, 'COPY018', 'CHECKED_OUT', 'Shelf F1', TRUE),
(7, 'COPY019', 'AVAILABLE', 'Shelf G1', TRUE),
(7, 'COPY020', 'AVAILABLE', 'Shelf G1', TRUE),
(8, 'COPY021', 'AVAILABLE', 'Shelf H1', TRUE),
(8, 'COPY022', 'DAMAGED', 'Shelf H1', TRUE),
(9, 'COPY023', 'AVAILABLE', 'Shelf I1', TRUE),
(9, 'COPY024', 'CHECKED_OUT', 'Shelf I1', TRUE),
(10, 'COPY025', 'AVAILABLE', 'Shelf J1', TRUE),
(10, 'COPY026', 'AVAILABLE', 'Shelf J1', TRUE)
ON CONFLICT (barcode) DO NOTHING;

-- Insert sample book records (loans)
INSERT INTO book_records (copyid, userid, checkoutdate, duedate, returndate, renewcount) VALUES
(1, 4, '2025-10-12 09:20:00', '2025-10-26 23:59:59', '2025-10-24 16:40:00', 0),
(3, 7, '2025-10-15 11:05:00', '2025-10-29 23:59:59', NULL, 1),
(4, 12, '2025-10-20 14:10:00', '2025-11-03 23:59:59', '2025-11-02 10:22:00', 0),
(6, 8, '2025-10-25 13:45:00', '2025-11-08 23:59:59', NULL, 0),
(7, 2, '2025-11-01 15:30:00', '2025-11-15 23:59:59', NULL, 1),
(8, 5, '2025-11-03 10:15:00', '2025-11-17 23:59:59', '2025-11-16 09:50:00', 0),
(9, 9, '2025-11-05 12:40:00', '2025-11-19 23:59:59', NULL, 2),
(10, 11, '2025-11-07 08:55:00', '2025-11-21 23:59:59', '2025-11-20 18:00:00', 1),
(11, 6, '2025-11-10 09:35:00', '2025-11-24 23:59:59', NULL, 0),
(12, 14, '2025-11-11 16:20:00', '2025-11-25 23:59:59', '2025-11-24 12:10:00', 0),
(13, 10, '2025-11-12 10:05:00', '2025-11-26 23:59:59', NULL, 1),
(14, 18, '2025-11-13 11:50:00', '2025-11-27 23:59:59', NULL, 0),
(15, 20, '2025-11-14 14:45:00', '2025-11-28 23:59:59', '2025-11-27 09:12:00', 0),
(16, 22, '2025-11-15 09:55:00', '2025-11-29 23:59:59', NULL, 2),
(17, 17, '2025-11-16 13:15:00', '2025-11-30 23:59:59', NULL, 0),
(18, 19, '2025-11-17 10:00:00', '2025-12-01 23:59:59', NULL, 1),
(19, 23, '2025-11-18 11:30:00', '2025-12-02 23:59:59', '2025-12-01 15:05:00', 0),
(20, 25, '2025-11-19 15:40:00', '2025-12-03 23:59:59', NULL, 0),
(21, 27, '2025-11-20 12:25:00', '2025-12-04 23:59:59', NULL, 1),
(22, 28, '2025-11-21 14:10:00', '2025-12-05 23:59:59', '2025-12-04 11:55:00', 0);

-- Insert sample fines
INSERT INTO fines (userid, loanid, amount, finedate, reason, status) VALUES
(1, 1, 5.50, '2025-11-20 09:00:00', 'Overdue book', 'UNPAID');

-- Insert sample holds
INSERT INTO holds (userid, titleid, copyid, status, placedat, readyat, pickupexpire, position) VALUES
(2, 1, 1, 'READY', '2025-11-15 11:00:00', '2025-11-20 15:00:00', '2025-11-27 23:59:59', 1);
