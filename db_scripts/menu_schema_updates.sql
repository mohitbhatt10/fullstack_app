-- =============================================
-- Restaurant Menu Database Schema Updates
-- =============================================

-- Add is_veg column to product table
ALTER TABLE product 
ADD COLUMN is_veg BOOLEAN DEFAULT TRUE
COMMENT 'Vegetarian indicator: TRUE for vegetarian, FALSE for non-vegetarian';

-- Add display_order column to category table
ALTER TABLE category 
ADD COLUMN display_order INT DEFAULT 0
COMMENT 'Order in which category appears on menu';

-- Update existing categories with display order
UPDATE category SET display_order = 1 WHERE name = 'Starters (Veg)';
UPDATE category SET display_order = 2 WHERE name = 'Starters (Non-Veg)';
UPDATE category SET display_order = 3 WHERE name = 'Quick Bites';
UPDATE category SET display_order = 4 WHERE name = 'Burgers & Sandwiches';
UPDATE category SET display_order = 5 WHERE name = 'Pizza';
UPDATE category SET display_order = 6 WHERE name = 'Pasta & Noodles';
UPDATE category SET display_order = 7 WHERE name = 'Main Course (Veg)';
UPDATE category SET display_order = 8 WHERE name = 'Main Course (Non-Veg)';
UPDATE category SET display_order = 9 WHERE name = 'Rice & Biryani';
UPDATE category SET display_order = 10 WHERE name = 'Indian Breads';
UPDATE category SET display_order = 11 WHERE name = 'Desserts';
UPDATE category SET display_order = 12 WHERE name = 'Beverages (Hot & Cold)';

-- =============================================
-- Sample Data Insertion (Optional)
-- Insert only if you want to populate with sample data
-- =============================================

-- Insert categories if they don't exist
INSERT IGNORE INTO category (name, display_order) VALUES
('Starters (Veg)', 1),
('Starters (Non-Veg)', 2),
('Quick Bites', 3),
('Burgers & Sandwiches', 4),
('Pizza', 5),
('Pasta & Noodles', 6),
('Main Course (Veg)', 7),
('Main Course (Non-Veg)', 8),
('Rice & Biryani', 9),
('Indian Breads', 10),
('Desserts', 11),
('Beverages (Hot & Cold)', 12);

-- Sample product insertions
-- Note: Replace category_fk with actual category IDs from your database

-- Starters (Veg) - Assuming category_fk = 1
INSERT INTO product (name, category_fk, description, price, status, is_veg) VALUES
('Paneer Tikka', 1, 'Cottage cheese cubes marinated in spices, grilled to perfection', 280, 'true', TRUE),
('Hara Bhara Kabab', 1, 'Spinach and green peas patties with Indian spices', 240, 'true', TRUE),
('Veg Spring Rolls', 1, 'Crispy rolls filled with seasoned vegetables', 220, 'true', TRUE),
('Mushroom Tikka', 1, 'Fresh mushrooms marinated in yogurt and spices', 260, 'true', TRUE),
('Crispy Corn', 1, 'Golden fried corn kernels tossed in tangy spices', 200, 'true', TRUE);

-- Starters (Non-Veg) - Assuming category_fk = 2
INSERT INTO product (name, category_fk, description, price, status, is_veg) VALUES
('Chicken Tikka', 2, 'Boneless chicken marinated in yogurt and spices, charcoal grilled', 320, 'true', FALSE),
('Fish Amritsari', 2, 'Crispy fried fish fillets with authentic Punjabi flavors', 360, 'true', FALSE),
('Chicken 65', 2, 'Spicy deep fried chicken with curry leaves and chilies', 300, 'true', FALSE),
('Tandoori Chicken', 2, 'Classic chicken marinated in tandoori masala and grilled', 340, 'true', FALSE);

-- Quick Bites - Assuming category_fk = 3
INSERT INTO product (name, category_fk, description, price, status, is_veg) VALUES
('Pav Bhaji', 3, 'Mumbai style spiced vegetable curry served with buttered buns', 180, 'true', TRUE),
('Vada Pav', 3, 'Spicy potato fritter in a bun with chutneys', 60, 'true', TRUE),
('Samosa Chaat', 3, 'Crispy samosas topped with yogurt, chutneys, and sev', 120, 'true', TRUE),
('Papdi Chaat', 3, 'Crispy flour crackers with potatoes, chickpeas, and tangy sauces', 100, 'true', TRUE),
('Dahi Puri', 3, 'Crispy puris filled with potatoes, yogurt, and sweet chutney', 110, 'true', TRUE);

-- Pizza - Assuming category_fk = 5
INSERT INTO product (name, category_fk, description, price, status, is_veg) VALUES
('Margherita Pizza', 5, 'Classic pizza with fresh mozzarella, tomato sauce, and basil', 280, 'true', TRUE),
('Farmhouse Pizza', 5, 'Loaded with mushrooms, capsicum, onions, and tomatoes', 320, 'true', TRUE),
('Paneer Tikka Pizza', 5, 'Indian fusion with paneer tikka and masala sauce', 360, 'true', TRUE),
('Chicken BBQ Pizza', 5, 'BBQ chicken with onions and bell peppers', 380, 'true', FALSE);

-- Main Course (Veg) - Assuming category_fk = 7
INSERT INTO product (name, category_fk, description, price, status, is_veg) VALUES
('Paneer Butter Masala', 7, 'Cottage cheese in rich tomato and butter gravy', 300, 'true', TRUE),
('Dal Makhani', 7, 'Slow cooked black lentils with butter and cream', 240, 'true', TRUE),
('Kadai Paneer', 7, 'Paneer cooked with bell peppers and onions in kadai masala', 290, 'true', TRUE),
('Malai Kofta', 7, 'Vegetable dumplings in creamy cashew gravy', 280, 'true', TRUE);

-- Main Course (Non-Veg) - Assuming category_fk = 8
INSERT INTO product (name, category_fk, description, price, status, is_veg) VALUES
('Butter Chicken', 8, 'Tender chicken in creamy tomato butter gravy', 340, 'true', FALSE),
('Chicken Tikka Masala', 8, 'Grilled chicken tikka in spiced tomato curry', 360, 'true', FALSE),
('Kadai Chicken', 8, 'Chicken cooked with bell peppers in kadai spices', 330, 'true', FALSE),
('Rogan Josh', 8, 'Aromatic mutton curry with Kashmiri spices', 420, 'true', FALSE);

-- Rice & Biryani - Assuming category_fk = 9
INSERT INTO product (name, category_fk, description, price, status, is_veg) VALUES
('Veg Biryani', 9, 'Fragrant basmati rice layered with mixed vegetables and spices', 260, 'true', TRUE),
('Chicken Biryani', 9, 'Aromatic rice cooked with chicken in traditional dum style', 320, 'true', FALSE),
('Mutton Biryani', 9, 'Premium biryani with tender mutton pieces', 400, 'true', FALSE),
('Jeera Rice', 9, 'Steamed basmati rice tempered with cumin', 140, 'true', TRUE);

-- Indian Breads - Assuming category_fk = 10
INSERT INTO product (name, category_fk, description, price, status, is_veg) VALUES
('Plain Naan', 10, 'Soft leavened bread baked in tandoor', 50, 'true', TRUE),
('Butter Naan', 10, 'Naan brushed with butter', 60, 'true', TRUE),
('Garlic Naan', 10, 'Naan topped with fresh garlic and coriander', 70, 'true', TRUE),
('Tandoori Roti', 10, 'Whole wheat bread baked in tandoor', 40, 'true', TRUE),
('Laccha Paratha', 10, 'Multi-layered whole wheat bread', 60, 'true', TRUE);

-- Desserts - Assuming category_fk = 11
INSERT INTO product (name, category_fk, description, price, status, is_veg) VALUES
('Gulab Jamun', 11, 'Soft milk dumplings soaked in cardamom syrup', 80, 'true', TRUE),
('Rasmalai', 11, 'Cottage cheese patties in sweetened milk', 100, 'true', TRUE),
('Ice Cream (Per Scoop)', 11, 'Choose from vanilla, chocolate, or strawberry', 60, 'true', TRUE),
('Brownie with Ice Cream', 11, 'Warm chocolate brownie topped with vanilla ice cream', 140, 'true', TRUE);

-- Beverages - Assuming category_fk = 12
INSERT INTO product (name, category_fk, description, price, status, is_veg) VALUES
('Masala Chai', 12, 'Traditional Indian spiced tea', 40, 'true', TRUE),
('Filter Coffee', 12, 'South Indian style filter coffee', 50, 'true', TRUE),
('Cold Coffee', 12, 'Chilled coffee with ice cream and milk', 120, 'true', TRUE),
('Fresh Lime Soda', 12, 'Refreshing lime juice with soda', 60, 'true', TRUE),
('Mango Lassi', 12, 'Creamy yogurt drink with mango pulp', 100, 'true', TRUE),
('Fresh Juice', 12, 'Choose from orange, watermelon, or pineapple', 80, 'true', TRUE);

-- =============================================
-- Verification Queries
-- =============================================

-- Check updated schema
DESCRIBE product;
DESCRIBE category;

-- View all categories with display order
SELECT id, name, display_order 
FROM category 
ORDER BY display_order;

-- View sample menu items with veg indicator
SELECT p.id, p.name, c.name as category, p.description, p.price, p.is_veg
FROM product p
JOIN category c ON p.category_fk = c.id
WHERE p.status = 'true'
ORDER BY c.display_order, p.name;
