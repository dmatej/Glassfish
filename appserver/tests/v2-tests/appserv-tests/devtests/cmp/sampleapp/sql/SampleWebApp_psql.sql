DROP TABLE COMPOSITEiNTsTRINGbEANtABLE cascade;

CREATE TABLE COMPOSITEINTSTRINGBEANTABLE (
    ID INTEGER,
    NAME VARCHAR(255),
    SALARY DOUBLE PRECISION NOT NULL,
    CONSTRAINT pk_CompositeIntStringBeanTabl PRIMARY KEY (ID, NAME)
);

commit;
