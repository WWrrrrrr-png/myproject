-- 데이터베이스 만들기 
DROP DATABASE IF EXISTS Urban_Outfitters_DB;               -- DROP DATABASE: DB 삭제 / IF EXISTS: 있을 때만(없으면 조용히 통과)
CREATE DATABASE Urban_Outfitters_DB                        -- CREATE DATABASE: 새 DB 생성(테이블 담는 큰 폴더)
  DEFAULT CHARACTER SET utf8mb4                            -- CHARACTER SET: 문자 인코딩(utf8mb4=이모지/모든 언어)
  DEFAULT COLLATE utf8mb4_0900_ai_ci;                      -- COLLATE: 정렬/비교 규칙(0900=MySQL8, ai=악센트 무시, ci=대소문자 무시)
USE Urban_Outfitters_DB;                                   -- USE: 이후의 모든 쿼리를 이 DB에 실행(작업 폴더 선택)

/* 
   1) 기본 테이블
 */

-- 1) 회원
DROP TABLE IF EXISTS MBSP_TBL;                             -- DROP TABLE: 테이블 삭제 / IF EXISTS: 있을 때만
CREATE TABLE MBSP_TBL(                                     -- CREATE TABLE: 테이블 생성
  MBSP_ID        VARCHAR(15) PRIMARY KEY,                  -- VARCHAR(15): 가변문자 최대 15자 / PRIMARY KEY: 행 고유키(중복/NULL 불가)
  MBSP_NAME      VARCHAR(30)  NOT NULL,                    -- NOT NULL: 반드시 값 필요
  MBSP_EMAIL     VARCHAR(50)  NOT NULL UNIQUE,             -- UNIQUE: 중복 금지(이메일 유일하게)
  MBSP_PASSWORD  CHAR(60)     NOT NULL,                    -- CHAR(60): 고정문자 60자(BCrypt 해시 길이)
  MBSP_PHONE     VARCHAR(20)  NOT NULL,                    -- 전화번호: 숫자+특수기호 가능하니 문자열로 저장
  MBSP_DATESUB   DATETIME     NOT NULL DEFAULT NOW(),      -- DATETIME: 날짜+시간 / DEFAULT NOW(): 현재시각 자동 입력
  MBSP_LASTLOGIN DATETIME NULL                             -- NULL 허용: 아직 로그인 안 했을 수 있음
) ENGINE=InnoDB;                                           -- ENGINE=InnoDB: 트랜잭션/외래키 지원 스토리지 엔진

-- 2) 카테고리 (자기참조 = 꼬리물기)
DROP TABLE IF EXISTS CATEGORY_TBL;
CREATE TABLE CATEGORY_TBL(                                  -- 카테고리 계층형 구조(부모-자식)
  CATE_CODE    INT AUTO_INCREMENT PRIMARY KEY,              -- INT: 정수 / AUTO_INCREMENT: 자동증가 / PK: 고유식별자
  CATE_PRTCODE INT NULL,                                    -- 부모 카테고리 코드(NULL이면 최상위 1차)
  CATE_NAME    VARCHAR(60) NOT NULL,                        -- 카테고리명
  CONSTRAINT FK_CATEGORY_PARENT                             -- CONSTRAINT 이름: 제약조건에 별칭 부여(가독성/관리용)
    FOREIGN KEY (CATE_PRTCODE) REFERENCES CATEGORY_TBL(CATE_CODE)  -- FOREIGN KEY: 외래키(같은 테이블의 PK 참조=자기참조)
    ON DELETE CASCADE                                       -- ON DELETE CASCADE: 부모 삭제 시 자식도 자동 삭제
) ENGINE=InnoDB;
CREATE INDEX IDX_CATEGORY_PARENT ON CATEGORY_TBL(CATE_PRTCODE);  -- INDEX: 조회속도 향상(부모코드로 자식 찾기 빠르게)

-- 3) 상품
DROP TABLE IF EXISTS PRODUCT_TBL;
CREATE TABLE PRODUCT_TBL(
  PRO_NUM        BIGINT AUTO_INCREMENT PRIMARY KEY,         -- BIGINT: 큰 정수(상품 많아져도 안전) / 자동증가 PK
  CATE_CODE      INT NULL,                                  -- FK 후보: 카테고리 코드(NULL=미분류 허용)
  PRO_NAME       VARCHAR(120) NOT NULL,                     -- 상품명
  PRO_PRICE      INT NOT NULL,                              -- 가격(원 단위 정수)
  PRO_DISCOUNT   TINYINT NOT NULL DEFAULT 0,                -- TINYINT: 작은 정수(0~100 가정) / DEFAULT 0: 기본 0%
  PRO_CONTENT    VARCHAR(2000) NOT NULL,                    -- 상품 설명(간단 텍스트는 VARCHAR로)
  PRO_STOCK      INT NOT NULL DEFAULT 0,                    -- 재고 수량 / 기본 0
  PRO_BUY        CHAR(1) NOT NULL DEFAULT 'Y',              -- 판매여부(Y/N) / CHAR(1): 고정 1글자
  PRO_DATE       DATETIME NOT NULL DEFAULT NOW(),           -- 등록일시
  PRO_UPDATEDATE DATETIME NOT NULL DEFAULT NOW(),           -- 수정일시(※ 자동 갱신 아님, 앱/트리거에서 갱신)
  CONSTRAINT FK_PRODUCT_CATEGORY
    FOREIGN KEY (CATE_CODE) REFERENCES CATEGORY_TBL(CATE_CODE)   -- 카테고리 테이블의 PK를 참조
    ON DELETE SET NULL                                           -- 카테고리 삭제 시 상품은 살리고 FK만 NULL 처리
) ENGINE=InnoDB;
CREATE INDEX IDX_PRODUCT_CAT  ON PRODUCT_TBL(CATE_CODE);    -- 카테고리별 상품 조회 최적화
CREATE INDEX IDX_PRODUCT_NAME ON PRODUCT_TBL(PRO_NAME);     -- 상품명 검색 최적화(부분일치 LIKE 등)

-- 4) 상품 이미지
DROP TABLE IF EXISTS PRODUCT_IMG_TBL;
CREATE TABLE PRODUCT_IMG_TBL(
  IMG_ID   BIGINT AUTO_INCREMENT PRIMARY KEY,               -- 이미지 PK
  PRO_NUM  BIGINT NOT NULL,                                 -- 어떤 상품의 이미지인지(FK)
  IMG_URL  VARCHAR(255) NOT NULL,                           -- 이미지 경로/URL
  IS_MAIN  CHAR(1) NOT NULL DEFAULT 'N',                    -- 대표이미지 여부(Y/N)
  CONSTRAINT FK_IMG_PRODUCT FOREIGN KEY (PRO_NUM)           -- 외래키: 상품 삭제 시
    REFERENCES PRODUCT_TBL(PRO_NUM) ON DELETE CASCADE       -- ON DELETE CASCADE: 이미지도 함께 삭제
) ENGINE=InnoDB;

-- 5) 장바구니(복합키: 회원+상품)
DROP TABLE IF EXISTS CART_TBL;
CREATE TABLE CART_TBL(
  MBSP_ID     VARCHAR(15) NOT NULL,                         -- 회원ID(FK)
  PRO_NUM     BIGINT NOT NULL,                              -- 상품ID(FK)
  CART_AMOUNT INT NOT NULL,                                 -- 수량
  CART_DATE   DATETIME NOT NULL DEFAULT NOW(),              -- 담은 시각
  PRIMARY KEY (MBSP_ID, PRO_NUM),                           -- 복합 PRIMARY KEY: (회원,상품) 조합 유일 → 중복담기 방지
  CONSTRAINT FK_CART_MEMBER  FOREIGN KEY (MBSP_ID)          -- 회원 삭제 시
    REFERENCES MBSP_TBL(MBSP_ID) ON DELETE CASCADE,         -- 카트 항목도 삭제
  CONSTRAINT FK_CART_PRODUCT FOREIGN KEY (PRO_NUM)          -- 상품 삭제 시
    REFERENCES PRODUCT_TBL(PRO_NUM) ON DELETE CASCADE       -- 카트 항목도 삭제
) ENGINE=InnoDB;

-- 6) 주문
DROP TABLE IF EXISTS ORDER_TBL;
CREATE TABLE ORDER_TBL(
  ORD_CODE        BIGINT AUTO_INCREMENT PRIMARY KEY,        -- 주문코드 PK
  MBSP_ID         VARCHAR(15) NOT NULL,                     -- 주문 회원ID(FK)
  ORD_NAME        VARCHAR(50) NOT NULL,                     -- 수령인 이름
  ORD_TEL         VARCHAR(20) NOT NULL,                     -- 전화
  ORD_MAIL        VARCHAR(50) NOT NULL,                     -- 이메일
  ORD_ADDR_ZIP    VARCHAR(10) NOT NULL,                     -- 우편번호
  ORD_ADDR_BASIC  VARCHAR(120) NOT NULL,                    -- 기본주소
  ORD_ADDR_DETAIL VARCHAR(120) NOT NULL,                    -- 상세주소
  ORD_PRICE       INT NOT NULL,                             -- 총 결제금액(할인가 합계)
  ORD_STATUS      VARCHAR(20) NOT NULL DEFAULT '입금대기',  -- 상태(기본 '입금대기')
  ORD_REGDATE     DATETIME NOT NULL DEFAULT NOW(),          -- 주문일시
  CONSTRAINT FK_ORDER_MEMBER FOREIGN KEY (MBSP_ID)          -- 주문자-회원 연결
    REFERENCES MBSP_TBL(MBSP_ID)
) ENGINE=InnoDB;

-- 7) 주문 상세(복합키: 주문+상품)
DROP TABLE IF EXISTS ORDETAIL_TBL;
CREATE TABLE ORDETAIL_TBL(
  ORD_CODE  BIGINT NOT NULL,                                -- 주문코드(FK)
  PRO_NUM   BIGINT NOT NULL,                                -- 상품코드(FK)
  DT_AMOUNT INT NOT NULL,                                   -- 수량
  DT_PRICE  INT NOT NULL,                                   -- 라인금액(할인단가×수량)
  PRIMARY KEY (ORD_CODE, PRO_NUM),                          -- 복합 PK: 한 주문 안에 같은 상품은 1행만
  CONSTRAINT FK_DETAIL_ORDER   FOREIGN KEY (ORD_CODE)       -- 주문 삭제 시
    REFERENCES ORDER_TBL(ORD_CODE)  ON DELETE CASCADE,      -- 해당 상세도 삭제
  CONSTRAINT FK_DETAIL_PRODUCT FOREIGN KEY (PRO_NUM)        -- 상품 삭제 시
    REFERENCES PRODUCT_TBL(PRO_NUM) ON DELETE CASCADE       -- 해당 라인도 삭제
) ENGINE=InnoDB;

-- 8) 결제
DROP TABLE IF EXISTS PAYMENT_TBL;
CREATE TABLE PAYMENT_TBL(
  PAYMENT_ID     BIGINT AUTO_INCREMENT PRIMARY KEY,         -- 결제 PK
  ORD_CODE       BIGINT NOT NULL,                           -- 주문 FK
  MBSP_ID        VARCHAR(15) NOT NULL,                      -- 회원ID(추적/감사용)
  PAYMENT_METHOD VARCHAR(20) NOT NULL,                      -- 결제수단(예: CARD/BANK/APPLEPAY)
  PAYMENT_PRICE  INT NOT NULL,                              -- 결제금액
  PAYMENT_STATUS VARCHAR(20) NOT NULL,                      -- 결제상태(PAID/FAIL/REFUND 등)
  PAYMENT_DATE   DATETIME NOT NULL DEFAULT NOW(),           -- 결제시각
  CONSTRAINT FK_PAYMENT_ORDER FOREIGN KEY (ORD_CODE)        -- 주문 삭제 시
    REFERENCES ORDER_TBL(ORD_CODE) ON DELETE CASCADE        -- 결제도 삭제
) ENGINE=InnoDB;

-- 9) 배송
DROP TABLE IF EXISTS DELIVERY_TBL;
CREATE TABLE DELIVERY_TBL(
  DELIVERY_ID     BIGINT AUTO_INCREMENT PRIMARY KEY,        -- 배송 PK
  ORD_CODE        BIGINT NOT NULL,                          -- 주문 FK
  SHIPPING_ZIP    VARCHAR(10) NOT NULL,                     -- 우편번호
  SHIPPING_ADDR   VARCHAR(120) NOT NULL,                    -- 주소
  SHIPPING_DEADDR VARCHAR(120) NOT NULL,                    -- 상세주소
  DELIVERY_DATE   DATETIME NULL,                            -- 배송일(발송/도착 등) / NULL 허용
  DELIVERY_STATUS VARCHAR(20) NOT NULL DEFAULT '발송준비',  -- 배송상태(기본 '발송준비')
  CONSTRAINT FK_DELIVERY_ORDER FOREIGN KEY (ORD_CODE)       -- 주문 삭제 시
    REFERENCES ORDER_TBL(ORD_CODE) ON DELETE CASCADE        -- 배송도 삭제
) ENGINE=InnoDB;

-- 10) 리뷰(선택)
DROP TABLE IF EXISTS REVIEW_TBL;
CREATE TABLE REVIEW_TBL(
  REV_CODE   BIGINT AUTO_INCREMENT PRIMARY KEY,             -- 리뷰 PK
  MBSP_ID    VARCHAR(15) NOT NULL,                          -- 작성자 회원ID(FK)
  PRO_NUM    BIGINT NOT NULL,                               -- 상품 FK
  REV_RATE   TINYINT NOT NULL,                              -- 평점(1~5) 작은 정수
  REV_CONTENT VARCHAR(400) NOT NULL,                        -- 리뷰 내용
  REV_DATE   DATETIME NOT NULL DEFAULT NOW(),               -- 작성시각
  CONSTRAINT FK_REVIEW_MEMBER  FOREIGN KEY (MBSP_ID)        -- 회원 삭제 시
    REFERENCES MBSP_TBL(MBSP_ID) ON DELETE CASCADE,         -- 리뷰 삭제
  CONSTRAINT FK_REVIEW_PRODUCT FOREIGN KEY (PRO_NUM)        -- 상품 삭제 시
    REFERENCES PRODUCT_TBL(PRO_NUM) ON DELETE CASCADE       -- 리뷰 삭제
) ENGINE=InnoDB;

/* 
   2) 초기 데이터 (UO 스타일 카테고리/상품)
   */

-- 회원 1명 (INSERT INTO: 데이터 삽입 / 컬럼목록→VALUES 값목록)
INSERT INTO MBSP_TBL(MBSP_ID,MBSP_NAME,MBSP_EMAIL,MBSP_PASSWORD,MBSP_PHONE)
VALUES ('demo','데모','demo@uo.com',
        '$2a$10$abcdefghijklmnopqrstuvwxyzABCDE1234567890abc',  -- 예시 해시
        '010-1111-2222');

-- 1차 카테고리(부모=NULL → 최상위)
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (NULL,'NEW');       -- NULL: 값 없음(루트)
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (NULL,'WOMEN');     -- 2
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (NULL,'MEN');       -- 3
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (NULL,'HOME');      -- 4
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (NULL,'BEAUTY');    -- 5
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (NULL,'LIFESTYLE'); -- 6
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (NULL,'SALE');      -- 7

-- 2차: WOMEN(부모=2)
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (2,'DRESSES');              -- 8
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (2,'TOPS');                 -- 9
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (2,'BOTTOMS');              -- 10
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (2,'DENIM');                -- 11
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (2,'SHOES & ACCESSORIES');  -- 12

-- 2차: MEN(부모=3)
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (3,'TOPS');                 -- 13
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (3,'BOTTOMS');              -- 14
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (3,'DENIM');                -- 15
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (3,'SHOES & ACCESSORIES');  -- 16

-- 2차: HOME(부모=4)
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (4,'FURNITURE');            -- 17
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (4,'BEDDING');              -- 18
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (4,'RUGS');                 -- 19
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (4,'KITCHEN & BAR');        -- 20
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (4,'DECOR & LIGHTING');     -- 21

-- 2차: BEAUTY(부모=5)
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (5,'SKINCARE');             -- 22
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (5,'MAKEUP');               -- 23
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (5,'HAIR');                 -- 24
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (5,'FRAGRANCE');            -- 25

-- 2차: LIFESTYLE(부모=6)
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (6,'TECH');                 -- 26
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (6,'BOOKS & MUSIC');        -- 27
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (6,'FITNESS');              -- 28
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (6,'OUTDOOR');              -- 29

-- 3차 예시: HOME > BEDDING(18)의 자식
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (18,'DUVET COVERS');
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (18,'SHEETS');
INSERT INTO CATEGORY_TBL(CATE_PRTCODE,CATE_NAME) VALUES (18,'PILLOWS');

-- 상품 예시(10개) — 여러 행 INSERT
INSERT INTO PRODUCT_TBL(CATE_CODE, PRO_NAME, PRO_PRICE, PRO_DISCOUNT, PRO_CONTENT, PRO_STOCK) VALUES
 (11,'BDG High-Rise Baggy Jean', 99000, 15,'여성 데님 하이라이즈 배기',120),
 (8 ,'UO Floral Midi Dress',    119000,20,'플로럴 미디 드레스',40),
 (9 ,'UO Seamless Baby Tee',     39000, 0,'심플 베이비 티',200),
 (12,'Platform Mary Jane',       89000,10,'메리제인 슈즈',60),
 (13,'Graphic Tee',              45000, 5,'남성 그래픽 티셔츠',150),
 (14,'Cargo Pants',              79000,10,'남성 카고 팬츠',80),
 (17,'Wood Side Table',         159000,15,'우드 사이드 테이블',25),
 (18,'Washed Cotton Duvet',     129000,20,'워시드 코튼 이불커버',35),
 (22,'Gentle Cleanser',          19000, 0,'저자극 클렌저',90),
 (26,'Bluetooth Turntable',     179000, 5,'블루투스 턴테이블',15);

-- 대표 이미지 더미 — INSERT ... SELECT: SELECT 결과를 INSERT에 바로 사용
INSERT INTO PRODUCT_IMG_TBL(PRO_NUM, IMG_URL, IS_MAIN)            -- 대상 컬럼 3개 명시
SELECT PRO_NUM, CONCAT('/uo/img/', PRO_NUM, '_main.jpg'), 'Y'     -- CONCAT: 문자열 이어붙이기
FROM PRODUCT_TBL;                                                 -- 모든 상품에 대해 한 장씩 생성

-- 1차 카테고리(부모 없음)
SELECT CATE_CODE, CATE_NAME                                      -- SELECT: 보여줄 컬럼 선택
FROM CATEGORY_TBL                                                 -- FROM: 데이터 출처(테이블)
WHERE CATE_PRTCODE IS NULL                                        -- WHERE: 행 필터 / IS NULL: NULL 비교 전용
ORDER BY CATE_CODE;                                               -- ORDER BY: 정렬(기본 오름차)

-- 특정 1차의 자식 (예: WOMEN = 2)
SELECT CATE_CODE, CATE_NAME
FROM CATEGORY_TBL
WHERE CATE_PRTCODE = 2;                                           -- = : 값 동등 비교

-- 재귀 CTE로 전체 트리 보기 (MySQL 8+)
WITH RECURSIVE cat AS (                                           -- WITH RECURSIVE: 재귀 공통테이블식(CTE)
  SELECT CATE_CODE, CATE_PRTCODE, CATE_NAME, 1 AS LV              -- 1 AS LV: 컬럼 별칭(AS=별명)
  FROM CATEGORY_TBL WHERE CATE_PRTCODE IS NULL                     -- 시작 집합(앵커): 1차 카테고리
  UNION ALL                                                        -- UNION ALL: 중복 제거 없이 이어 붙이기
  SELECT c.CATE_CODE, c.CATE_PRTCODE, c.CATE_NAME, cat.LV+1       -- cat.LV+1: 레벨 증가 / AS 생략 가능
  FROM CATEGORY_TBL c JOIN cat ON c.CATE_PRTCODE = cat.CATE_CODE  -- JOIN: 두 집합 연결 / ON: 연결 조건
)                                                                 -- c.CATE_PRTCODE(자식의 부모코드) = cat.CATE_CODE(부모)
SELECT LPAD('', (LV-1)*2, ' ') AS INDENT, CATE_CODE, CATE_NAME, LV -- LPAD로 들여쓰기 / AS INDENT: 출력 컬럼명
FROM cat
ORDER BY CATE_PRTCODE, CATE_CODE;                                 -- 트리 느낌으로 정렬

-- 카테고리별 상품 (예: HOME > BEDDING = 18)
SELECT PRO_NUM, PRO_NAME, PRO_PRICE, PRO_DISCOUNT
FROM PRODUCT_TBL WHERE CATE_CODE = 18
ORDER BY PRO_NUM DESC;                                            -- DESC: 내림차순

-- 할인가 계산 포함
SELECT PRO_NUM, PRO_NAME,
       PRO_PRICE,
       PRO_DISCOUNT,
       ROUND(PRO_PRICE*(100-PRO_DISCOUNT)/100,0) AS SALE_PRICE    -- ROUND(x,0): 반올림 / AS: 컬럼 별칭
FROM PRODUCT_TBL
ORDER BY PRO_NUM DESC;
