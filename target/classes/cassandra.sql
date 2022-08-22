--docker run --rm -it --network cassandra nuvo/docker-cqlsh cqlsh cassandra 9042 --cqlversion='3.4.5'

CREATE KEYSPACE IF NOT EXISTS wls WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };

CREATE TABLE IF NOT EXISTS wls.menu_nodes( 
	nodeId int, 
	parentNodeId int,
	nodeName text,
	url text,
	PRIMARY KEY ( nodeId)
);

CREATE INDEX nodeIdIndex ON wls.menu_nodes(parentNodeId);

INSERT INTO wls.menu_nodes ( nodeId, parentNodeId, nodeName, url) VALUES (1,0,'Domain List','gen/listDomains');
INSERT INTO wls.menu_nodes ( nodeId, parentNodeId, nodeName, url) VALUES (2,0,'List Applications','gen/listApplications');
INSERT INTO wls.menu_nodes ( nodeId, parentNodeId, nodeName, url) VALUES (3,0,'List DataSources','gen/listDataSources');
INSERT INTO wls.menu_nodes ( nodeId, parentNodeId, nodeName, url) VALUES (3,0,'List Managed Server','gen/listManagedServers');


CREATE TABLE IF NOT EXISTS wls.monitoring_domains( 
	domain_id int, 
	domainName text,
	userName text,
	password text,
	url text,
	lob text,
	created_time timestamp,
	PRIMARY KEY (domain_id)
);


CREATE TABLE IF NOT EXISTS wls.tenant_servers ( 
	report_id int, 
	name text,
	state text,
	health text,
	stats_time timestamp,
	PRIMARY KEY (report_id)
);


CREATE TABLE IF NOT EXISTS wls.tenant_servers_server ( 
	report_id int,
	name text,
	state text,
	health text,
	clusterName text, 
	currentMachine text, 
	weblogicVersion text, 
	openSocketsCurrentCount int, 
	heapFreeCurrent int, 
	javaVersion int,
	osName text,
	osVersion text,
	jvmProcessorLoad int,
	stats_time timestamp,
	PRIMARY KEY (report_id)
);


CREATE TABLE IF NOT EXISTS wls.tenant_clusters ( 
	report_id int, 
	name text,
	servers text,
	stats_time timestamp,
	PRIMARY KEY (report_id)
);

CREATE TABLE IF NOT EXISTS wls.tenant_applications ( 
	report_id int, 
	name text,
	type text,
	health timestamp,
	PRIMARY KEY (report_id)
);


CREATE TABLE IF NOT EXISTS wls.tenant_datasources ( 
	report_id int, 
	name text,
	type text,
	instances timestamp,
	PRIMARY KEY (report_id)
);


CREATE TABLE IF NOT EXISTS wls.tenant_datasources_datasource ( 
	report_id int, 
	name text,
	type text,
	instances timestamp,
	PRIMARY KEY (report_id)
);
