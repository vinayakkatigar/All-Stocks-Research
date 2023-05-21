
  CREATE TABLE SENSEX_STOCK_INFO (	
SENSEX_STOCK_INFO_ID NUMBER(10,0) GENERATED BY DEFAULT ON NULL AS IDENTITY ( START WITH 1 INCREMENT BY 1 MINVALUE 1 CACHE 20 NOORDER  NOCYCLE  ), 
	STOCK_NAME VARCHAR2(200), 
    STOCK_URL VARCHAR2(200), 
    STOCK_RANK NUMBER (10,2),
    STOCK_MKT_CAP NUMBER (10,2),
    CURRENT_MARKET_PRICE NUMBER (10,2),
    YEARLY_LOW NUMBER (10,2),
    YEARLY_HIGH NUMBER (10,2),
    YEARLY_HIGH_LOW_DIFF NUMBER (10,2),
    YEARLY_HIGH_DIFF NUMBER (10,2),
    YEARLY_LOW_DIFF NUMBER (10,2),
    FII_PCT NUMBER (10,2),
    EPS NUMBER (10,2),
    P2EPS NUMBER (10,2),
    BOOK_VALUE NUMBER (10,2),
    PRICE_TO_BOOK_VALUE NUMBER (10,2),
    QUOTETS VARCHAR2(200), 

    PRIMARY KEY (SENSEX_STOCK_INFO_ID)
     )


CREATE SEQUENCE SENSEX_STOCK_INFO_ID_SEQ START WITH 1 INCREMENT BY 1 MINVALUE 1 CACHE 20 NOCYCLE NOORDER;