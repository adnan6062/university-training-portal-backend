INSERT IGNORE INTO notification_templates (name, subject, body, type, description, active) VALUES
('ENROLLMENT_CONFIRMATION', 'Enrollment Confirmed', 'Dear {name}, your enrollment in {course} has been confirmed. Your enrollment ID is {enrollmentId}.', 'EMAIL', 'Sent when enrollment is confirmed', true),
('INVOICE_CREATED', 'Invoice Created', 'Dear {name}, an invoice of {amount} has been created for your enrollment. Due date: {dueDate}.', 'EMAIL', 'Sent when invoice is created', true),
('PAYMENT_SUCCESS', 'Payment Successful', 'Dear {name}, your payment of {amount} has been received. Your enrollment is now active.', 'EMAIL', 'Sent when payment is successful', true),
('CERTIFICATE_ISSUED', 'Certificate Issued', 'Congratulations {name}! Your certificate for {course} has been issued. Certificate number: {certNumber}.', 'EMAIL', 'Sent when certificate is issued', true),
('ASSIGNMENT_GRADED', 'Assignment Graded', 'Dear {name}, your assignment has been graded. Score: {score}. Feedback: {feedback}.', 'EMAIL', 'Sent when assignment is graded', true),
('EXAM_REMINDER', 'Upcoming Exam Reminder', 'Dear {name}, you have an exam scheduled for {examDate}. Duration: {duration} minutes.', 'EMAIL', 'Sent as exam reminder', true);
