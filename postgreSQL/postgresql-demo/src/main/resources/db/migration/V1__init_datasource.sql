-- 1. 建表
-- 2. 建表后DDL语句的同步
-- 3. CRUD 以及分页是否收到影响

-- 创建父表
CREATE TABLE mc_message (
    id varchar(50),
    title varchar(1000) NOT NULL,
    content varchar(10000) NOT NULL,
    sender_id int8 NOT NULL,
    receive_id int8 NOT NULL,
    status int2 NOT NULL,
    create_time timestamp,
    CONSTRAINT "mc_message_pk" PRIMARY KEY ("id", "create_time")
) PARTITION BY RANGE(create_time);

-- 创建分区表
CREATE TABLE mc_message_history PARTITION OF mc_message FOR VALUES FROM ('2000-01-01 00:00:00') TO ('2019-12-31 23:59:59');
CREATE TABLE mc_message_2020 PARTITION OF mc_message FOR VALUES FROM ('2020-01-01 00:00:00') TO ('2020-12-31 23:59:59');
CREATE TABLE mc_message_2021 PARTITION OF mc_message FOR VALUES FROM ('2021-01-01 00:00:00') TO ('2021-12-31 23:59:59');
CREATE TABLE mc_message_2022 PARTITION OF mc_message FOR VALUES FROM ('2022-01-01 00:00:00') TO ('2022-12-31 23:59:59');
CREATE TABLE mc_message_2023 PARTITION OF mc_message FOR VALUES FROM ('2023-01-01 00:00:00') TO ('2023-12-31 23:59:59');

-- 在分区上创建索引
CREATE INDEX mc_message_idx_history_create_time ON mc_message_history USING btree(create_time);
CREATE INDEX mc_message_idx_2020_create_time ON mc_message_2020 USING btree(create_time);
CREATE INDEX mc_message_idx_2021_create_time ON mc_message_2021 USING btree(create_time);
CREATE INDEX mc_message_idx_2022_create_time ON mc_message_2022 USING btree(create_time);
CREATE INDEX mc_message_idx_2023_create_time ON mc_message_2023 USING btree(create_time);


INSERT INTO mc_message (id, title, content, sender_id, receive_id, status, create_time)
select uuid_generate_v4(), 'title', 'content', 1000, 2000, 1, generate_series('2000-01-01 00:00:00'::date, '2022-12-30 00:00:00'::date, '10 minute');