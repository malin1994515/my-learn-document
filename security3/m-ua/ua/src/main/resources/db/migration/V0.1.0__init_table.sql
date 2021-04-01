DROP TABLE IF EXISTS "oauth_client_details";
CREATE TABLE "oauth_client_details" (
	"client_id" VARCHAR ( 48 ) NOT NULL,
	"resource_ids" VARCHAR ( 256 ) DEFAULT NULL,
	"client_secret" VARCHAR ( 256 ) DEFAULT NULL,
	"scope" VARCHAR ( 256 ) DEFAULT NULL,
	"authorized_grant_types" VARCHAR ( 256 ) DEFAULT NULL,
	"web_server_redirect_uri" VARCHAR ( 256 ) DEFAULT NULL,
	"authorities" VARCHAR ( 256 ) DEFAULT NULL,
	"access_token_validity" int8 DEFAULT NULL,
	"refresh_token_validity" int8 DEFAULT NULL,
	"additional_information" VARCHAR ( 4096 ) DEFAULT NULL,
	"autoapprove" VARCHAR ( 256 ) DEFAULT NULL,
    CONSTRAINT "oauth_client_details_pkey" PRIMARY KEY ( "client_id" )
);

INSERT INTO "oauth_client_details"("client_id", "resource_ids", "client_secret", "scope", "authorized_grant_types", "web_server_redirect_uri", "authorities", "access_token_validity", "refresh_token_validity", "additional_information", "autoapprove")
VALUES ('client1', 'res1', '123', 'all', 'authorization_code,refresh_token', 'http://127.0.0.1:1000/autologin', NULL, 7200, 259200, NULL, 'true');
INSERT INTO "oauth_client_details"("client_id", "resource_ids", "client_secret", "scope", "authorized_grant_types", "web_server_redirect_uri", "authorities", "access_token_validity", "refresh_token_validity", "additional_information", "autoapprove")
VALUES ('client2', 'res1', '123', 'all', 'authorization_code,refresh_token', 'http://127.0.0.1:2000/autologin', NULL, 7200, 259200, NULL, 'true');