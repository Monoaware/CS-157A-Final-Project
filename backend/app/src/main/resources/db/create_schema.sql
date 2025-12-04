-- Drop existing tables if they exist (for development/testing purposes)
DROP TABLE IF EXISTS holds CASCADE;
DROP TABLE IF EXISTS fines CASCADE;
DROP TABLE IF EXISTS book_records CASCADE;
DROP TABLE IF EXISTS copies CASCADE;
DROP TABLE IF EXISTS titles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    fname VARCHAR(50) NOT NULL,
    lname VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    passwordhash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL
);

-- Titles table
CREATE TABLE IF NOT EXISTS titles (
    id SERIAL PRIMARY KEY,
    ISBN VARCHAR(255) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    yearPublished INT NOT NULL,
    genre VARCHAR(100),
    isVisible BOOLEAN DEFAULT TRUE
);

-- Copies table
CREATE TABLE IF NOT EXISTS copies (
    copyid SERIAL PRIMARY KEY,
    titleid INTEGER NOT NULL REFERENCES titles(id),
    barcode VARCHAR(100) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL,
    location VARCHAR(100),
    isvisible BOOLEAN NOT NULL
);

-- Book records (loans) table
CREATE TABLE IF NOT EXISTS book_records (
    loanid SERIAL PRIMARY KEY,
    copyid INTEGER NOT NULL REFERENCES copies(copyid),
    userid INTEGER NOT NULL REFERENCES users(id),
    checkoutdate TIMESTAMP NOT NULL,
    duedate TIMESTAMP NOT NULL,
    returndate TIMESTAMP,
    renewcount INTEGER NOT NULL
);

-- Fines table
CREATE TABLE IF NOT EXISTS fines (
    id SERIAL PRIMARY KEY,
    userid INTEGER NOT NULL REFERENCES users(id),
    loanid INTEGER NOT NULL REFERENCES book_records(loanid),
    amount NUMERIC(10,2) NOT NULL,
    finedate TIMESTAMP NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL
);

-- Holds table
CREATE TABLE IF NOT EXISTS holds (
    id SERIAL PRIMARY KEY,
    userid INTEGER NOT NULL REFERENCES users(id),
    titleid INTEGER NOT NULL REFERENCES titles(id),
    copyid INTEGER REFERENCES copies(copyid),
    status VARCHAR(20) NOT NULL,
    placedat TIMESTAMP NOT NULL,
    readyat TIMESTAMP NULL,
    pickupexpire TIMESTAMP NULL,
    position INTEGER NOT NULL
);