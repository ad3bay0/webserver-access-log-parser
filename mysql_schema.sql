USE parser;

DROP TABLE IF EXISTS logs, blocked_ips;

CREATE TABLE logs(id INT PRIMARY KEY AUTO_INCREMENT, request_ip VARCHAR(50), request_date TIMESTAMP, request_method VARCHAR(50), request_status VARCHAR(10), request_agent TEXT,created_date TIMESTAMP);
CREATE TABLE blocked_ips(id INT PRIMARY KEY AUTO_INCREMENT, request_ip VARCHAR(100), block_message TEXT, created_date TIMESTAMP);
