-- Test Data for Database Integration Tests
-- This SQL file is loaded by Spring's @Sql annotation for testing

-- Insert test quotes with various scenarios
INSERT INTO quotes (id, premium, monthly_premium, coverage_amount, deductible, valid_until, created_at, 
                   vehicle_make, vehicle_model, vehicle_year, vehicle_vin, vehicle_current_value, 
                   primary_driver_name, primary_driver_license) VALUES 
('SQL-001', 1200.00, 100.00, 100000.00, 1000.00, '2025-09-14', '2025-08-14 20:00:00',
 'Honda', 'Civic', 2020, '1HGFC2F53JA000001', 25000.00, 'John SQL Driver', 'SQL123456'),

('SQL-002', 1500.00, 125.00, 150000.00, 1500.00, '2025-09-14', '2025-08-14 20:01:00',
 'Toyota', 'Camry', 2021, '4T1C11AK8JU000002', 30000.00, 'Jane SQL Driver', 'SQL234567'),

('SQL-003', 2000.00, 166.67, 200000.00, 2000.00, '2025-09-14', '2025-08-14 20:02:00',
 'BMW', 'X5', 2022, '5UXCR6C03N9000003', 50000.00, 'Bob SQL Driver', 'SQL345678');

-- Insert discount data for the test quotes
INSERT INTO quote_discounts (quote_id, discount_description) VALUES
('SQL-001', 'Safe Driver Discount'),
('SQL-001', 'Multi-Policy Discount'),
('SQL-002', 'Good Student Discount'),
('SQL-002', 'Low Mileage Discount'),
('SQL-002', 'Anti-Theft Discount'),
('SQL-003', 'Loyalty Discount');