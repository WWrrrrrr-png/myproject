
-- 데이터베이스 만들기 (https://www.fashionnova.com/)
CREATE DATABASE IF NOT EXISTS fashionnova_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

    USE fashionnova_db;

-- 권한(필요 시)
GRANT ALL PRIVILEGES ON fashionnova_db.* TO 'Catarinakim'@'%';
FLUSH PRIVILEGES;





-- 회원 (회원가입 정보)
CREATE TABLE members (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  email        VARCHAR(150) NOT NULL UNIQUE,         -- 로그인 아이디 (이메일)
  password     VARCHAR(200) NOT NULL,                -- 비밀번호(암호화 저장)
  name         VARCHAR(100) NOT NULL,                -- 이름
  phone        VARCHAR(20) NULL,                     -- 휴대폰번호
  zipcode      VARCHAR(10) NULL,                     -- 우편번호
  address      VARCHAR(200) NULL,                    -- 기본 주소
  address2     VARCHAR(200) NULL,                    -- 상세 주소
  gender       ENUM('M','F','OTHER') NULL,           -- 성별
  birthdate    DATE NULL,                            -- 생년월일
  is_active    TINYINT(1) NOT NULL DEFAULT 1,        -- 탈퇴 여부
  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB; 

-- 주문 (주문 기본 정보 / 주문 상세 / 배송지)
CREATE TABLE IF NOT EXISTS orders (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  member_id      BIGINT NOT NULL,                 -- 주문한 회원
  order_number   VARCHAR(50) NOT NULL UNIQUE,     -- 주문번호
  order_status   ENUM('PENDING','PAID','SHIPPED','DELIVERED','CANCELLED')
                 NOT NULL DEFAULT 'PENDING',
  total_amount   DECIMAL(10,2) NOT NULL,          -- 결제 총액
  payment_method ENUM('CARD','BANK','PAYPAL','KAKAOPAY','NAVERPAY') NOT NULL,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_orders_member
    FOREIGN KEY (member_id) REFERENCES members(id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- 주문 상세 (상품 단위)
CREATE TABLE IF NOT EXISTS order_items (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id     BIGINT NOT NULL,                   -- 주문 번호
  product_id   BIGINT NOT NULL,                   -- 상품
  variant_id   BIGINT NULL,                       -- 상품 옵션
  quantity     INT NOT NULL DEFAULT 1,            -- 수량
  unit_price   DECIMAL(10,2) NOT NULL,            -- 단가
  subtotal     DECIMAL(10,2) NOT NULL,            -- 소계
  CONSTRAINT fk_items_order
    FOREIGN KEY (order_id) REFERENCES orders(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_items_product
    FOREIGN KEY (product_id) REFERENCES products(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_items_variant
    FOREIGN KEY (variant_id) REFERENCES product_variants(id)
    ON DELETE SET NULL
) ENGINE=InnoDB;



-- 1) 카테고리 (1차/2차까지 가벼운 트리/계층 구조)
CREATE TABLE IF NOT EXISTS categories (
  id           INT AUTO_INCREMENT PRIMARY KEY,     -- 카테고리 고유번호
  parent_id    INT NULL,                           -- 상위 카테고리 (없으면 NULL)
  name         VARCHAR(100) NOT NULL,              -- 카테고리명
  slug         VARCHAR(120) NOT NULL UNIQUE,       -- URL용 코드
  depth        TINYINT NOT NULL DEFAULT 1,         -- 뎁스 (1=1차, 2=2차)
  sort_order   INT NOT NULL DEFAULT 0,             -- 정렬순서
  is_active    TINYINT(1) NOT NULL DEFAULT 1,      -- 사용 여부
  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_categories_parent
    FOREIGN KEY (parent_id) REFERENCES categories(id)
    ON DELETE SET NULL
) ENGINE=InnoDB;

-- 주문 배송지 (주문 시점에 따로 저장 = 주소 바뀌어도 이력 유지)
CREATE TABLE IF NOT EXISTS order_shipping (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id     BIGINT NOT NULL,
  recipient    VARCHAR(100) NOT NULL,             -- 수령인
  phone        VARCHAR(20) NOT NULL,
  zipcode      VARCHAR(10) NOT NULL,
  address      VARCHAR(200) NOT NULL,
  address2     VARCHAR(200) NULL,
  request_msg  VARCHAR(200) NULL,                 -- 배송메모
  CONSTRAINT fk_shipping_order
    FOREIGN KEY (order_id) REFERENCES orders(id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

SELECT o.order_number, o.order_status, o.total_amount, o.created_at
FROM orders o
JOIN members m ON m.id = o.member_id
WHERE m.email = 'hong@test.com';

SELECT oi.quantity, oi.unit_price, oi.subtotal, p.name, pv.color, pv.size_label
FROM order_items oi
JOIN products p ON p.id = oi.product_id
LEFT JOIN product_variants pv ON pv.id = oi.variant_id
WHERE oi.order_id = 1;


-- 2) 상품
CREATE TABLE IF NOT EXISTS products (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY, -- 상품 고유번호
  category_id   INT NULL,                          -- 소속 카테고리
  name          VARCHAR(200) NOT NULL,             -- 상품명
  slug          VARCHAR(220) NOT NULL UNIQUE,      -- URL용 코드
  summary       VARCHAR(500) NULL,                 -- 한 줄 설명
  base_price    DECIMAL(10,2) NOT NULL,            -- 기본가
  status        ENUM('ACTIVE','HIDDEN') NOT NULL DEFAULT 'ACTIVE', -- 상태
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_products_category
    FOREIGN KEY (category_id) REFERENCES categories(id)
    ON DELETE SET NULL,
  INDEX ix_products_status_created (status, created_at)
) ENGINE=InnoDB;

--  상품 이미지 (대표/추가)
CREATE TABLE IF NOT EXISTS product_images (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id  BIGINT NOT NULL,
  image_url   VARCHAR(500) NOT NULL,              -- 이미지 경로/URL
  is_main     TINYINT(1) NOT NULL DEFAULT 0,      -- 대표 이미지 여부
  sort_order  INT NOT NULL DEFAULT 0,
  CONSTRAINT fk_images_product
    FOREIGN KEY (product_id) REFERENCES products(id)
    ON DELETE CASCADE,
  INDEX ix_images_main (product_id, is_main, sort_order)
) ENGINE=InnoDB;

--  옵션(색상/사이즈) + 재고/가격 변동이 있으면 여기서 관리
CREATE TABLE IF NOT EXISTS product_variants (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id  BIGINT NOT NULL,
  sku         VARCHAR(100) NOT NULL UNIQUE,       -- 재고코드
  color       VARCHAR(50) NULL,
  size_label  VARCHAR(30) NULL,
  price       DECIMAL(10,2) NULL,                 -- 옵션별 가격
  stock_qty   INT NOT NULL DEFAULT 0,             -- 재고 수량
  is_default  TINYINT(1) NOT NULL DEFAULT 0,      -- 대표 옵션 여부
  CONSTRAINT fk_variants_product
    FOREIGN KEY (product_id) REFERENCES products(id)
    ON DELETE CASCADE,
  INDEX ix_variants_product (product_id, is_default)
) ENGINE=InnoDB;

--  배너(슬라이드) : 홈 상단/중단/하단 등 위치별 관리
CREATE TABLE IF NOT EXISTS banners (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  title       VARCHAR(150) NULL,                  -- 배너 제목(선택)
  image_url   VARCHAR(500) NOT NULL,              -- 배너 이미지
  link_url    VARCHAR(500) NULL,                  -- 클릭 시 이동
  position    ENUM('HERO','MID','FOOTER') NOT NULL DEFAULT 'HERO', -- 위치
  sort_order  INT NOT NULL DEFAULT 0,             -- 순서
  is_active   TINYINT(1) NOT NULL DEFAULT 1,      -- 노출 여부
  start_at    DATETIME NULL,                      -- 시작일시(선택)
  end_at      DATETIME NULL,                      -- 종료일시(선택)
  INDEX ix_banners_active (position, is_active, start_at, end_at)
) ENGINE=InnoDB;

--  컬렉션 : 신상품/베스트/세일/트렌딩 등
CREATE TABLE IF NOT EXISTS collections(
  id          INT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100) NOT NULL,                            -- 표시 이름
  code        ENUM('NEW_IN','BEST_SELLERS','FLASH_SALE','TRENDING','CUSTOM')
              NOT NULL DEFAULT 'CUSTOM',                        -- 유형 코드
  description VARCHAR(300) NULL,
  sort_order  INT NOT NULL DEFAULT 0,
  is_active   TINYINT(1) NOT NULL DEFAULT 1,
  UNIQUE KEY uq_collections_code_name (code, name)
) ENGINE=InnoDB;

--  컬렉션-상품
CREATE TABLE collection_items (
  collection_id INT NOT NULL,
  product_id    BIGINT NOT NULL,
  sort_order    INT NOT NULL DEFAULT 0,
  PRIMARY KEY (collection_id, product_id),
  CONSTRAINT fk_ci_collection
    FOREIGN KEY (collection_id) REFERENCES collections(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_ci_product
    FOREIGN KEY (product_id) REFERENCES products(id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- 8) 할인(기간 세일) : 간단 버전(상품 기준)
CREATE TABLE IF NOT EXISTS discounts (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id  BIGINT NOT NULL,
  type        ENUM('PERCENT','AMOUNT') NOT NULL,  -- 할인 방식
  value       DECIMAL(10,2) NOT NULL,             -- 할인값
  start_at    DATETIME NOT NULL,
  end_at      DATETIME NOT NULL,
  is_active   TINYINT(1) NOT NULL DEFAULT 1,
  CONSTRAINT fk_discounts_product
    FOREIGN KEY (product_id) REFERENCES products(id)
    ON DELETE CASCADE,
  INDEX ix_discounts_active (product_id, is_active, start_at, end_at)
) ENGINE=InnoDB;

--  현재 판매가 뽑는 뷰(기본가 vs 할인가)
CREATE OR REPLACE VIEW v_product_price AS
SELECT
  p.id          AS product_id,
  p.name,
  p.slug,
  p.base_price,
  -- 활성 할인 한 개만 가정(가장 큰 할인 우선)
  CASE
    WHEN d.id IS NOT NULL AND d.type = 'PERCENT'
      THEN ROUND(p.base_price * (100 - d.value)/100, 2)
    WHEN d.id IS NOT NULL AND d.type = 'AMOUNT'
      THEN GREATEST(0, p.base_price - d.value)
    ELSE p.base_price
  END AS sale_price
FROM products p
LEFT JOIN (
  SELECT d1.*
  FROM discounts d1
  WHERE d1.is_active = 1
    AND NOW() BETWEEN d1.start_at AND d1.end_at
) d ON d.product_id = p.id; 


-- 카테고리
INSERT IGNORE INTO categories (id, parent_id, name, slug, depth, sort_order, is_active)
VALUES
(1, NULL, 'Women', 'women', 1, 1, 1),
(2, NULL, 'Men',   'men',   1, 2, 1),
(3, 1, 'Dresses', 'dresses', 2, 1, 1),
(4, 1, 'Tops',    'tops',    2, 2, 1);

-- 상품
INSERT IGNORE INTO products (id, category_id, name, slug, summary, base_price, status)
VALUES
(1, 3, 'Bodycon Mini Dress', 'bodycon-mini-dress', '슬림 핏 미니 드레스', 39000, 'ACTIVE'),
(2, 4, 'Cropped Ribbed Top', 'cropped-ribbed-top', '골지 크롭 탑',       19000, 'ACTIVE'),
(3, 3, 'Satin Slip Dress',   'satin-slip-dress',   '새틴 슬립 드레스',    59000, 'ACTIVE');

-- 상품 이미지(대표 이미지 한 장씩)
INSERT IGNORE INTO product_images (id, product_id, image_url, is_main, sort_order) VALUES
(1, 1, 'https://cdn.example.com/img/dress1_main.jpg', 1, 1),
(2, 2, 'https://cdn.example.com/img/top1_main.jpg',   1, 1),
(3, 3, 'https://cdn.example.com/img/dress2_main.jpg', 1, 1);
-- 옵션/재고(간단)
INSERT IGNORE INTO product_variants (id, product_id, sku, color, size_label, price, stock_qty, is_default)
VALUES
(1, 1, 'SKU-DRS-001-BLK-S', 'Black', 'S', NULL, 10, 1),
(2, 1, 'SKU-DRS-001-BLK-M', 'Black', 'M', NULL,  8, 0),
(3, 2, 'SKU-TOP-001-IVR-F', 'Ivory', 'F', 17000, 25, 1),
(4, 3, 'SKU-DRS-002-PNK-S', 'Pink',  'S', NULL, 12, 1);

-- 배너
INSERT IGNORE INTO banners (id, title, image_url, link_url, position, sort_order, is_active, start_at, end_at)
VALUES
(1, 'NEW IN WEEKLY', 'https://cdn.example.com/banners/hero_newin.jpg', '/collection/new', 'HERO', 1, 1, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)),
(2, 'FLASH SALE', 'https://cdn.example.com/banners/hero_sale.jpg', '/collection/flash', 'HERO', 2, 1, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY));

-- 컬렉션(신상품/베스트/세일)
INSERT IGNORE INTO collections (id, name, code, description, sort_order, is_active) VALUES
(1, 'New In', 'NEW_IN', '이번 주 신상품', 1, 1),
(2, 'Best Sellers', 'BEST_SELLERS', '많이 팔린 아이템', 2, 1),
(3, 'Flash Sale', 'FLASH_SALE', '기간 한정 특가', 3, 1);

-- 컬렉션-상품 매핑
INSERT IGNORE INTO collection_items (collection_id, product_id, sort_order) VALUES
(1, 1, 1), (1, 2, 2), (1, 3, 3),     -- New In
(2, 1, 1),                           -- Best
(3, 2, 1);                           -- Flash

-- 할인(Flash Sale: 탑 10% 할인)
INSERT IGNORE INTO discounts (id, product_id, type, value, start_at, end_at, is_active)
VALUES
(1, 2, 'PERCENT', 10, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 1);

-- 홈 화면에 바로 쓰는 쿼리 예시
-- 히어로 배너
SELECT id, title, image_url, link_url
FROM banners
WHERE position = 'HERO'
  AND is_active = 1
  AND (start_at IS NULL OR NOW() >= start_at)
  AND (end_at   IS NULL OR NOW() <= end_at)
ORDER BY sort_order ASC, id DESC; 

-- 신상품(New In) 12개 
SELECT p.id, p.name, vp.sale_price, pi.image_url
FROM collection_items ci
JOIN collections c     ON c.id = ci.collection_id AND c.code = 'NEW_IN' AND c.is_active = 1
JOIN products   p      ON p.id = ci.product_id AND p.status = 'ACTIVE'
LEFT JOIN v_product_price vp ON vp.product_id = p.id
LEFT JOIN product_images pi  ON pi.product_id = p.id AND pi.is_main = 1
ORDER BY ci.sort_order ASC, p.created_at DESC
LIMIT 12;

-- 베스트셀러 8개(주문 데이터가 없으니 컬렉션으로 수동 선정)
SELECT p.id, p.name, vp.sale_price, pi.image_url
FROM collection_items ci
JOIN collections c ON c.id = ci.collection_id AND c.code = 'BEST_SELLERS' AND c.is_active = 1
JOIN products p    ON p.id = ci.product_id AND p.status = 'ACTIVE'
LEFT JOIN v_product_price vp ON vp.product_id = p.id
LEFT JOIN product_images pi  ON pi.product_id = p.id AND pi.is_main = 1
ORDER BY ci.sort_order ASC, p.id DESC
LIMIT 8;

-- 플래시 세일 8개(현재 활성 할인 상품)
SELECT p.id, p.name, p.base_price, vp.sale_price, pi.image_url
FROM products p
JOIN v_product_price vp ON vp.product_id = p.id
LEFT JOIN product_images pi ON pi.product_id = p.id AND pi.is_main = 1
WHERE vp.sale_price < p.base_price
ORDER BY (p.base_price - vp.sale_price) DESC, p.id DESC
LIMIT 8;

-- 1차 카테고리
SELECT id, name, slug
FROM categories
WHERE depth = 1 AND is_active = 1
ORDER BY sort_order ASC, id ASC;



