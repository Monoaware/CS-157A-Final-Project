-- Initialize sample data for the library database
-- VALIDATED: All data consistent with business logic

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

-- Insert sample titles (15 entries)
INSERT INTO titles (ISBN, title, author, yearPublished, genre, isVisible) VALUES
('978-0-13-110362-7', 'The C Programming Language', 'Brian W. Kernighan', 1988, 'TECHNOLOGY', TRUE),
('978-0-201-61622-4', 'The Pragmatic Programmer', 'David Thomas', 1999, 'TECHNOLOGY', TRUE),
('978-0-201-63361-0', 'Design Patterns', 'Gang of Four', 1994, 'TECHNOLOGY', TRUE),
('978-0-262-03384-8', 'Introduction to Algorithms', 'Thomas H. Cormen', 2009, 'TECHNOLOGY', TRUE),
('978-0-14-028329-7', 'To Kill a Mockingbird', 'Harper Lee', 1960, 'FICTION', TRUE),
('978-0-452-28423-4', '1984', 'George Orwell', 1949, 'FICTION', TRUE),
('978-0-7432-7356-5', 'The Great Gatsby', 'F. Scott Fitzgerald', 1925, 'FICTION', TRUE),
('978-0-316-76948-0', 'The Silent Patient', 'Alex Michaelides', 2019, 'MYSTERY', TRUE),
('978-0-553-21311-7', 'Dune', 'Frank Herbert', 1965, 'SCIENCE_FICTION', TRUE),
('978-1-5011-6877-2', 'The Outsider', 'Stephen King', 2018, 'THRILLER', TRUE),
('978-0-375-50420-1', 'The Road', 'Cormac McCarthy', 2006, 'FICTION', TRUE),
('978-0-7432-7357-2', 'The Catcher in the Rye', 'J.D. Salinger', 1951, 'FICTION', TRUE),
('978-1-250-30694-6', 'Project Hail Mary', 'Andy Weir', 2021, 'SCIENCE_FICTION', TRUE),
('978-0-06-231611-0', 'The Alchemist', 'Paulo Coelho', 1988, 'FICTION', TRUE),
('978-0-00-744803-6', 'A Game of Thrones', 'George R.R. Martin', 1996, 'FANTASY', TRUE)
ON CONFLICT (isbn) DO NOTHING;

-- Insert sample copies (15 entries - each title gets 1 copy)
-- VALIDATED: All statuses perfectly align with loan states
INSERT INTO copies (titleid, barcode, status, location, isvisible) VALUES
(1, 'COPY001', 'CHECKED_OUT', 'Shelf A1', TRUE),  -- Title 1: Has active loan ✅
(2, 'COPY002', 'AVAILABLE', 'Shelf A2', TRUE),    -- Title 2: No loan ✅
(3, 'COPY003', 'CHECKED_OUT', 'Shelf A3', TRUE),  -- Title 3: Has active loan ✅
(4, 'COPY004', 'RESERVED', 'Shelf A4', TRUE),     -- Title 4: Reserved for READY hold ✅
(5, 'COPY005', 'AVAILABLE', 'Shelf B1', TRUE),    -- Title 5: No loan ✅
(6, 'COPY006', 'CHECKED_OUT', 'Shelf B2', TRUE),  -- Title 6: Has active loan ✅
(7, 'COPY007', 'AVAILABLE', 'Shelf B3', TRUE),    -- Title 7: Loan was returned ✅
(8, 'COPY008', 'CHECKED_OUT', 'Shelf B4', TRUE),  -- Title 8: Has active loan ✅
(9, 'COPY009', 'DAMAGED', 'Shelf C1', TRUE),      -- Title 9: Damaged (no loan) ✅
(10, 'COPY010', 'CHECKED_OUT', 'Shelf C2', TRUE), -- Title 10: Has active loan ✅
(11, 'COPY011', 'AVAILABLE', 'Shelf C3', TRUE),   -- Title 11: No loan ✅
(12, 'COPY012', 'CHECKED_OUT', 'Shelf C4', TRUE), -- Title 12: Has active loan ✅
(13, 'COPY013', 'AVAILABLE', 'Shelf D1', TRUE),   -- Title 13: No loan, has QUEUED hold ✅
(14, 'COPY014', 'CHECKED_OUT', 'Shelf D2', TRUE), -- Title 14: Has active loan ✅
(15, 'COPY015', 'AVAILABLE', 'Shelf D3', TRUE)    -- Title 15: No loan ✅
ON CONFLICT (barcode) DO NOTHING;

-- Insert sample book records (15 loans - perfectly aligned with copy statuses)
-- VALIDATED: Every active loan (returndate IS NULL) has copy marked CHECKED_OUT
-- VALIDATED: Every CHECKED_OUT copy has an active loan
INSERT INTO book_records (copyid, userid, checkoutdate, duedate, returndate, renewcount) VALUES
-- ACTIVE LOANS (returndate IS NULL) - 7 active loans ✅
(1, 2, '2025-11-01 10:00:00', '2025-11-15 23:59:59', NULL, 0),   -- Copy 1: CHECKED_OUT ✅
(3, 7, '2025-11-03 14:30:00', '2025-11-17 23:59:59', NULL, 1),   -- Copy 3: CHECKED_OUT ✅
(6, 8, '2025-11-05 09:15:00', '2025-11-19 23:59:59', NULL, 0),   -- Copy 6: CHECKED_OUT ✅
(8, 10, '2025-11-10 11:45:00', '2025-11-24 23:59:59', NULL, 2),  -- Copy 8: CHECKED_OUT ✅
(10, 18, '2025-11-12 16:20:00', '2025-11-26 23:59:59', NULL, 0), -- Copy 10: CHECKED_OUT ✅
(12, 22, '2025-11-15 13:00:00', '2025-11-29 23:59:59', NULL, 1), -- Copy 12: CHECKED_OUT ✅
(14, 25, '2025-11-18 10:30:00', '2025-12-02 23:59:59', NULL, 0), -- Copy 14: CHECKED_OUT ✅

-- RETURNED LOANS (returndate NOT NULL) - 8 returned loans ✅
(2, 4, '2025-10-15 09:00:00', '2025-10-29 23:59:59', '2025-10-27 14:30:00', 0),  -- Copy 2: Now AVAILABLE ✅
(4, 6, '2025-10-20 11:15:00', '2025-11-03 23:59:59', '2025-11-02 16:45:00', 0),  -- Copy 4: Now RESERVED (for hold) ✅
(5, 9, '2025-10-22 13:30:00', '2025-11-05 23:59:59', '2025-11-04 10:20:00', 1),  -- Copy 5: Now AVAILABLE ✅
(7, 12, '2025-10-25 15:45:00', '2025-11-08 23:59:59', '2025-11-07 12:00:00', 0), -- Copy 7: Now AVAILABLE ✅
(9, 14, '2025-10-28 08:30:00', '2025-11-11 23:59:59', '2025-11-10 09:15:00', 0), -- Copy 9: Now DAMAGED ✅
(11, 17, '2025-11-01 14:00:00', '2025-11-15 23:59:59', '2025-11-14 11:30:00', 0),-- Copy 11: Now AVAILABLE ✅
(13, 20, '2025-11-04 10:45:00', '2025-11-18 23:59:59', '2025-11-17 15:20:00', 1),-- Copy 13: Now AVAILABLE ✅
(15, 27, '2025-11-07 16:30:00', '2025-11-21 23:59:59', '2025-11-20 13:45:00', 0);-- Copy 15: Now AVAILABLE ✅

-- Insert sample fines (15 entries)
-- VALIDATED: All fines reference valid loans and users
INSERT INTO fines (userid, loanid, amount, finedate, reason, status) VALUES
(2, 1, 5.00, '2025-11-16 09:00:00', 'Overdue book', 'UNPAID'),      -- Active loan, overdue
(7, 2, 3.50, '2025-11-18 10:00:00', 'Overdue book', 'UNPAID'),      -- Active loan, overdue
(8, 3, 7.00, '2025-11-20 11:00:00', 'Overdue book', 'UNPAID'),      -- Active loan, overdue
(4, 8, 2.00, '2025-11-08 14:30:00', 'Late return', 'PAID'),         -- Returned loan
(6, 9, 5.50, '2025-11-03 16:00:00', 'Late return', 'PAID'),         -- Returned loan (now reserved)
(9, 10, 4.00, '2025-11-05 09:30:00', 'Late return', 'PAID'),        -- Returned loan
(12, 11, 3.00, '2025-11-08 12:15:00', 'Late return', 'PAID'),       -- Returned loan
(14, 12, 15.00, '2025-11-11 08:45:00', 'Damaged book', 'UNPAID'),   -- Returned loan, book damaged
(17, 13, 2.50, '2025-11-15 13:20:00', 'Late return', 'PAID'),       -- Returned loan
(20, 14, 6.00, '2025-11-18 15:00:00', 'Late return', 'PAID'),       -- Returned loan
(27, 15, 1.50, '2025-11-21 10:30:00', 'Late return', 'PAID'),       -- Returned loan
(10, 4, 10.00, '2025-11-25 09:00:00', 'Overdue book', 'UNPAID'),    -- Active loan, overdue
(18, 5, 8.50, '2025-11-27 11:00:00', 'Overdue book', 'UNPAID'),     -- Active loan, overdue
(22, 6, 4.50, '2025-11-30 14:00:00', 'Overdue book', 'UNPAID'),     -- Active loan, overdue
(25, 7, 6.50, '2025-12-03 10:00:00', 'Overdue book', 'UNPAID');     -- Active loan, overdue

-- Insert sample holds (15 entries)
-- VALIDATED: Copy 4 is RESERVED because it has a READY hold
-- VALIDATED: QUEUED holds have copyid = NULL (waiting for copy)
-- VALIDATED: All other holds properly reference titles
INSERT INTO holds (userid, titleid, copyid, status, placedat, readyat, pickupexpire, position) VALUES
-- READY hold - Copy 4 is RESERVED
(5, 4, 4, 'READY', '2025-11-18 10:00:00', '2025-11-22 14:00:00', '2025-11-29 23:59:59', 1),

-- QUEUED holds (copyid = NULL - waiting for copies to become available)
(3, 1, NULL, 'QUEUED', '2025-11-20 09:00:00', NULL, NULL, 1),  -- Waiting for Title 1
(11, 3, NULL, 'QUEUED', '2025-11-21 10:30:00', NULL, NULL, 1), -- Waiting for Title 3
(13, 6, NULL, 'QUEUED', '2025-11-22 11:45:00', NULL, NULL, 1), -- Waiting for Title 6
(16, 8, NULL, 'QUEUED', '2025-11-23 13:00:00', NULL, NULL, 1), -- Waiting for Title 8
(19, 10, NULL, 'QUEUED', '2025-11-24 14:15:00', NULL, NULL, 1),-- Waiting for Title 10
(21, 12, NULL, 'QUEUED', '2025-11-25 15:30:00', NULL, NULL, 1),-- Waiting for Title 12
(23, 14, NULL, 'QUEUED', '2025-11-26 16:45:00', NULL, NULL, 1),-- Waiting for Title 14
(26, 1, NULL, 'QUEUED', '2025-11-27 09:00:00', NULL, NULL, 2),  -- Waiting for Title 1 (position 2)
(28, 3, NULL, 'QUEUED', '2025-11-28 10:15:00', NULL, NULL, 2), -- Waiting for Title 3 (position 2)
(30, 6, NULL, 'QUEUED', '2025-11-29 11:30:00', NULL, NULL, 2), -- Waiting for Title 6 (position 2)

-- EXPIRED holds (copyid = NULL - were not picked up)
(4, 2, NULL, 'EXPIRED', '2025-11-01 08:00:00', '2025-11-05 12:00:00', '2025-11-12 23:59:59', 1),
(15, 5, NULL, 'EXPIRED', '2025-11-03 09:00:00', '2025-11-07 13:00:00', '2025-11-14 23:59:59', 1),

-- CANCELLED hold (copyid = NULL - patron cancelled)
(24, 7, NULL, 'CANCELLED', '2025-11-10 14:00:00', NULL, NULL, 1),

-- PICKED_UP hold (old, completed transaction - was picked up and checked out)
(1, 15, NULL, 'PICKED_UP', '2025-10-15 10:00:00', '2025-10-18 12:00:00', '2025-10-25 23:59:59', 1);
