-- Quick Validation Query for initialize_data.sql
-- Run this after loading the data to verify consistency

BEGIN;

-- Count all entries
SELECT '=== ENTRY COUNTS ===' AS section;
SELECT 'Titles' AS table_name, COUNT(*) AS count FROM titles
UNION ALL
SELECT 'Copies', COUNT(*) FROM copies
UNION ALL
SELECT 'Book Records', COUNT(*) FROM book_records
UNION ALL
SELECT 'Fines', COUNT(*) FROM fines
UNION ALL
SELECT 'Holds', COUNT(*) FROM holds;

-- Verify CHECKED_OUT copies have active loans
SELECT '=== CHECKED_OUT VALIDATION ===' AS section;
SELECT 
    'CHECKED_OUT copies without active loans' AS issue,
    COUNT(*) AS problem_count
FROM copies c
LEFT JOIN book_records br ON c.copyid = br.copyid AND br.returndate IS NULL
WHERE c.status = 'CHECKED_OUT' AND br.loanid IS NULL;

-- Verify active loans have CHECKED_OUT copies
SELECT 
    'Active loans without CHECKED_OUT copies' AS issue,
    COUNT(*) AS problem_count
FROM book_records br
JOIN copies c ON br.copyid = c.copyid
WHERE br.returndate IS NULL AND c.status != 'CHECKED_OUT';

-- Verify RESERVED copies have READY holds
SELECT '=== RESERVED VALIDATION ===' AS section;
SELECT 
    'RESERVED copies without READY holds' AS issue,
    COUNT(*) AS problem_count
FROM copies c
LEFT JOIN holds h ON c.copyid = h.copyid AND h.status = 'READY'
WHERE c.status = 'RESERVED' AND h.id IS NULL;

-- Verify READY holds have RESERVED copies
SELECT 
    'READY holds without RESERVED copies' AS issue,
    COUNT(*) AS problem_count
FROM holds h
LEFT JOIN copies c ON h.copyid = c.copyid
WHERE h.status = 'READY' AND (c.copyid IS NULL OR c.status != 'RESERVED');

-- Verify QUEUED holds have NULL copyid
SELECT '=== QUEUED HOLDS VALIDATION ===' AS section;
SELECT 
    'QUEUED holds with copyid assigned' AS issue,
    COUNT(*) AS problem_count
FROM holds
WHERE status = 'QUEUED' AND copyid IS NOT NULL;

-- Verify EXPIRED/CANCELLED holds have NULL copyid
SELECT 
    'EXPIRED/CANCELLED holds with copyid still assigned' AS issue,
    COUNT(*) AS problem_count
FROM holds
WHERE status IN ('EXPIRED', 'CANCELLED') AND copyid IS NOT NULL;

-- Show summary statistics
SELECT '=== SUMMARY STATISTICS ===' AS section;
SELECT 
    c.status,
    COUNT(*) AS count,
    COUNT(CASE WHEN br.loanid IS NOT NULL THEN 1 END) AS active_loans,
    COUNT(CASE WHEN h.id IS NOT NULL THEN 1 END) AS ready_holds
FROM copies c
LEFT JOIN book_records br ON c.copyid = br.copyid AND br.returndate IS NULL
LEFT JOIN holds h ON c.copyid = h.copyid AND h.status = 'READY'
GROUP BY c.status
ORDER BY c.status;

-- Show hold status breakdown
SELECT 
    status,
    COUNT(*) AS count,
    COUNT(copyid) AS copies_assigned
FROM holds
GROUP BY status
ORDER BY status;

SELECT '=== VALIDATION COMPLETE ===' AS section;
SELECT 
    CASE 
        WHEN (SELECT COUNT(*) FROM copies WHERE status = 'CHECKED_OUT') = 
             (SELECT COUNT(*) FROM book_records WHERE returndate IS NULL)
        THEN 'ALL VALIDATIONS PASSED'
        ELSE 'INCONSISTENCIES FOUND'
    END AS result;

ROLLBACK;
