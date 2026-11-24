USE accessibility_mapper;

-- Local/demo credentials are "mapaccess"; change these before using a shared environment.
INSERT INTO users (name, email, password_hash, role) VALUES
('Mapper Admin', 'admin@access.org', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN'),
('Alex Morgan', 'mapper@access.org', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER');

INSERT INTO facilities (name, category, address, latitude, longitude, wheelchair_ramp,
    working_elevator, accessible_washroom, tactile_paving, wide_doorways, is_verified,
    is_approved, cover_image_url, last_accessibility_check, wheelchair_ramp_checked_at,
    working_elevator_checked_at, accessible_washroom_checked_at, tactile_paving_checked_at,
    wide_doorways_checked_at) VALUES
('Cubbon Park', 'Parks', 'Kasturba Road, Sampangi Rama Nagar, Bangalore',
 12.9763, 77.5929, TRUE, FALSE, TRUE, TRUE, TRUE, TRUE, TRUE,
 'https://images.unsplash.com/photo-1511497584788-876760111969?auto=format&fit=crop&w=960&q=80',
 NOW(), NOW(), NOW(), NOW(), NOW(), NOW()),
('Lalbagh Botanical Garden', 'Parks', 'Mavalli, Bangalore',
 12.9507, 77.5848, TRUE, FALSE, FALSE, TRUE, FALSE, FALSE, TRUE,
 'https://images.unsplash.com/photo-1470252649378-9c29740c9fa8?auto=format&fit=crop&w=960&q=80',
 NOW(), NOW(), NOW(), NOW(), NOW(), NOW()),
('Orion Mall', 'Malls', 'Brigade Gateway, Dr Rajkumar Road, Bangalore',
 13.0108, 77.5548, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE,
 'https://images.unsplash.com/photo-1519567241046-7f570eee3ce6?auto=format&fit=crop&w=960&q=80',
 NOW(), NOW(), NOW(), NOW(), NOW(), NOW()),
('UB City', 'Malls', 'Vittal Mallya Road, Ashok Nagar, Bangalore',
 12.9719, 77.6016, TRUE, TRUE, TRUE, FALSE, TRUE, FALSE, TRUE,
 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?auto=format&fit=crop&w=960&q=80',
 NOW(), NOW(), NOW(), NOW(), NOW(), NOW()),
('PVR INOX Vega City', 'Theatres', 'Vega City Mall, Bannerghatta Road, Bangalore',
 12.9069, 77.6018, TRUE, TRUE, TRUE, FALSE, TRUE, TRUE, TRUE,
 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=960&q=80',
 NOW(), NOW(), NOW(), NOW(), NOW(), NOW()),
('INOX Lido Mall', 'Theatres', 'Lido Mall, Ulsoor Road, Bangalore',
 12.9721, 77.6181, TRUE, TRUE, TRUE, TRUE, TRUE, FALSE, TRUE,
 'https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=960&q=80',
 NOW(), NOW(), NOW(), NOW(), NOW(), NOW());

INSERT INTO reviews (facility_id, user_id, rating, comments, is_approved,
    report_wheelchair_ramp, report_working_elevator, report_accessible_washroom,
    report_tactile_paving, report_wide_doorways) VALUES
(1, 2, 5, 'Checked the Kasturba Road entrance: step-free paths, accessible washrooms, and tactile paving were available along the main route.',
 TRUE, TRUE, NULL, TRUE, TRUE, TRUE),
(2, 2, 4, 'A step-free route and tactile paving were available at the west gate. Washroom access still needs another check.',
 TRUE, TRUE, NULL, FALSE, TRUE, NULL),
(3, 2, 5, 'The west entry ramp was clear. Lifts and the accessible washroom on the mall level were in use during my visit.',
 TRUE, TRUE, TRUE, TRUE, NULL, TRUE),
(4, 2, 4, 'Ramps, lifts, and wide entrances were in good shape. Check the directory for the accessible washroom location.',
 TRUE, TRUE, TRUE, NULL, NULL, TRUE),
(5, 2, 4, 'Step-free mall entry and working lifts. The accessible washroom was open when I visited.',
 TRUE, TRUE, TRUE, TRUE, NULL, TRUE),
(6, 2, 4, 'Ramps, lifts, washrooms, and tactile guidance were easy to find. Adding a recent access report for cinema visitors.',
 TRUE, TRUE, TRUE, TRUE, TRUE, TRUE);
