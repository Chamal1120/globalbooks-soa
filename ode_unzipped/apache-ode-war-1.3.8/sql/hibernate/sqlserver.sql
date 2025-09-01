--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements.  See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership.  The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License.  You may obtain a copy of the License at
--
--    http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied.  See the License for the
-- specific language governing permissions and limitations
-- under the License.
--
 
create table ODE_SCHEMA_VERSION(VERSION integer);
insert into ODE_SCHEMA_VERSION values (6);
-- Apache ODE - SimpleScheduler Database Schema
-- 
-- Apache Derby scripts by Maciej Szefler.
-- 
-- 

CREATE TABLE ode_job (
  jobid CHAR(64)  NOT NULL DEFAULT '',
  ts BIGINT  NOT NULL DEFAULT 0,
  nodeid char(64),
  scheduled int  NOT NULL DEFAULT 0,
  transacted int  NOT NULL DEFAULT 0,

  instanceId BIGINT,
  mexId varchar(255),
  processId varchar(255),
  type varchar(255),
  channel varchar(255),
  correlatorId varchar(255),
  correlationKeySet varchar(255),
  retryCount int,
  inMem int,
  detailsExt blob(4096),

  PRIMARY KEY(jobid));

CREATE INDEX IDX_ODE_JOB_TS ON ode_job(ts);
CREATE INDEX IDX_ODE_JOB_NODEID ON ode_job(nodeid);



    create table STORE_DU (
        NAME varchar(255) not null,
        deployer varchar(255),
        DEPLOYDT datetime,
        DIR varchar(255),
        primary key (NAME)
    );

    create table STORE_PROCESS (
        PID varchar(255) not null,
        DU varchar(255),
        TYPE varchar(255),
        version numeric(19,0),
        STATE varchar(255),
        primary key (PID)
    );

    create table STORE_PROCESS_PROP (
        propId varchar(255) not null,
        data text,
        name varchar(255) not null,
        primary key (propId, name)
    );

    create table STORE_VERSIONS (
        ID int not null,
        VERSION numeric(19,0),
        primary key (ID)
    );

    create table BPEL_ACTIVITY_RECOVERY (
        ID numeric(19,0) not null,
        PIID numeric(19,0),
        AID numeric(19,0),
        CHANNEL varchar(255),
        REASON varchar(255),
        DATE_TIME datetime,
        DETAILS image,
        ACTIONS varchar(255),
        RETRIES int,
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    create table BPEL_CORRELATION_PROP (
        ID numeric(19,0) not null,
        NAME varchar(255),
        NAMESPACE varchar(255),
        VALUE varchar(255),
        CORR_SET_ID numeric(19,0),
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    create table BPEL_CORRELATION_SET (
        ID numeric(19,0) not null,
        VALUE varchar(255),
        CORR_SET_NAME varchar(255),
        SCOPE_ID numeric(19,0),
        PIID numeric(19,0),
        PROCESS_ID numeric(19,0),
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    create table BPEL_CORRELATOR (
        ID numeric(19,0) not null,
        CID varchar(255),
        PROCESS_ID numeric(19,0),
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    create table BPEL_CORRELATOR_MESSAGE_CKEY (
        ID numeric(19,0) not null,
        CKEY varchar(255),
        CORRELATOR_MESSAGE_ID numeric(19,0),
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    create table BPEL_EVENT (
        ID numeric(19,0) not null,
        IID numeric(19,0),
        PID numeric(19,0),
        TSTAMP datetime,
        TYPE varchar(255),
        DETAIL text,
        DATA image,
        SID numeric(19,0),
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    create table BPEL_FAULT (
        ID numeric(19,0) not null,
        FAULTNAME varchar(255),
        DATA image,
        EXPLANATION varchar(4000),
        LINE_NUM int,
        AID int,
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    create table BPEL_INSTANCE (
        ID numeric(19,0) not null,
        INSTANTIATING_CORRELATOR numeric(19,0),
        FAULT numeric(19,0),
        JACOB_STATE_DATA image,
        PREVIOUS_STATE smallint,
        PROCESS_ID numeric(19,0),
        STATE smallint,
        LAST_ACTIVE_DT datetime,
        SEQUENCE numeric(19,0),
        FAILURE_COUNT int,
        FAILURE_DT datetime,
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    create table BPEL_MESSAGE (
        ID numeric(19,0) not null,
        MEX numeric(19,0),
        TYPE varchar(255),
        MESSAGE_DATA image,
        MESSAGE_HEADER image,
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    create table BPEL_MESSAGE_EXCHANGE (
        ID numeric(19,0) not null,
        PORT_TYPE varchar(255),
        CHANNEL_NAME varchar(255),
        CLIENTKEY varchar(255),
        ENDPOINT image,
        CALLBACK_ENDPOINT image,
        REQUEST numeric(19,0),
        RESPONSE numeric(19,0),
        INSERT_DT datetime,
        OPERATION varchar(255),
        STATE varchar(255),
        PROCESS numeric(19,0),
        PIID numeric(19,0),
        DIR char(1),
        PLINK_MODELID int,
        PATTERN varchar(255),
        CORR_STATUS varchar(255),
        FAULT_TYPE varchar(255),
        FAULT_EXPL varchar(255),
        CALLEE varchar(255),
        PARTNERLINK numeric(19,0),
        PIPED_ID varchar(255),
        SUBSCRIBER_COUNT int,
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    create table BPEL_MEX_PROPS (
        MEX numeric(19,0) not null,
        VALUE varchar(8000),
        NAME varchar(255) not null,
        primary key (MEX, NAME)
    );

    create table BPEL_PLINK_VAL (
        ID numeric(19,0) not null,
        PARTNER_LINK varchar(100) not null,
        PARTNERROLE varchar(100),
        MYROLE_EPR_DATA image,
        PARTNERROLE_EPR_DATA image,
        PROCESS numeric(19,0),
        SCOPE numeric(19,0),
        SVCNAME varchar(255),
        MYROLE varchar(100),
        MODELID int,
        MYSESSIONID varchar(255),
        PARTNERSESSIONID varchar(255),
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    create table BPEL_PROCESS (
        ID numeric(19,0) not null,
        PROCID varchar(255) not null,
        deployer varchar(255),
        deploydate datetime,
        type_name varchar(255),
        type_ns varchar(255),
        version numeric(19,0),
        ACTIVE_ bit,
        guid varchar(255),
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    create table BPEL_SCOPE (
        ID numeric(19,0) not null,
        PIID numeric(19,0),
        PARENT_SCOPE_ID numeric(19,0),
        STATE varchar(255) not null,
        NAME varchar(255) not null,
        MODELID int,
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    create table BPEL_SELECTORS (
        ID numeric(19,0) not null,
        PIID numeric(19,0) not null,
        SELGRPID varchar(255) not null,
        IDX int not null,
        CORRELATION_KEY varchar(255) not null,
        PROC_TYPE varchar(255) not null,
        ROUTE_POLICY varchar(255),
        CORRELATOR numeric(19,0) not null,
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    create table BPEL_UNMATCHED (
        ID numeric(19,0) not null,
        MEX numeric(19,0),
        CORRELATION_KEY varchar(255),
        CORRELATOR numeric(19,0) not null,
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    create table BPEL_XML_DATA (
        ID numeric(19,0) not null,
        DATA image,
        NAME varchar(255) not null,
        SIMPLE_VALUE varchar(255),
        SCOPE_ID numeric(19,0),
        PIID numeric(19,0),
        IS_SIMPLE_TYPE bit,
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    create table VAR_PROPERTY (
        ID numeric(19,0) not null,
        XML_DATA_ID numeric(19,0),
        PROP_VALUE varchar(255),
        PROP_NAME varchar(255) not null,
        INSERT_TIME datetime,
        MLOCK int not null,
        primary key (ID)
    );

    alter table BPEL_PROCESS 
        add constraint UNIQ_PROCID unique (PROCID);

    alter table BPEL_SELECTORS 
        add constraint UNIQ_SELECTOR unique (CORRELATION_KEY, CORRELATOR);

    create table hibernate_unique_key (
         next_hi int 
    );

    insert into hibernate_unique_key values ( 0 );

CREATE INDEX IDX_CORRELATOR_CID on BPEL_CORRELATOR (CID);
CREATE INDEX IDX_BPEL_CORRELATOR_PROCESS_ID on BPEL_CORRELATOR(PROCESS_ID);
CREATE INDEX IDX_BPEL_CORRELATOR_MSG_CKEY on BPEL_CORRELATOR_MESSAGE_CKEY (CKEY);
CREATE INDEX IDX_SELECTOR_SELGRPID on BPEL_SELECTORS (SELGRPID);
CREATE INDEX IDX_SELECTOR_CKEY on BPEL_SELECTORS (CORRELATION_KEY);
CREATE INDEX IDX_SELECTOR_CORRELATOR on BPEL_SELECTORS (CORRELATOR);
CREATE INDEX IDX_SELECTOR_INSTANCE on BPEL_SELECTORS (PIID);
CREATE INDEX IDX_BPEL_SELECTORS_PROC_TYPE on BPEL_SELECTORS(PROC_TYPE);
CREATE INDEX IDX_UNMATCHED_CORRELATOR on BPEL_UNMATCHED (CORRELATOR);
CREATE INDEX IDX_UNMATCHED_CKEY on BPEL_UNMATCHED (CORRELATION_KEY);
CREATE INDEX IDX_UNMATCHED_CORRELATOR_CKEY on BPEL_UNMATCHED (CORRELATOR,CORRELATION_KEY);
CREATE INDEX IDX_UNMATCHED_MEX on BPEL_UNMATCHED (MEX);
CREATE INDEX IDX_XMLDATA_IID on BPEL_XML_DATA (PIID);
CREATE INDEX IDX_XMLDATA_SID on BPEL_XML_DATA (SCOPE_ID);
CREATE INDEX IDX_XMLDATA_NAME on BPEL_XML_DATA (NAME);
CREATE INDEX IDX_XMLDATA_NAME_SID on BPEL_XML_DATA (NAME, SCOPE_ID);
CREATE INDEX IDX_EVENT_IID on BPEL_EVENT (IID);
CREATE INDEX IDX_EVENT_PID on BPEL_EVENT (PID);
CREATE INDEX IDX_CORR_SET_NAME on BPEL_CORRELATION_SET (CORR_SET_NAME);
CREATE INDEX IDX_CORR_SET_SCOPE_ID on BPEL_CORRELATION_SET (SCOPE_ID);
CREATE INDEX IDX_BPEL_INSTANCE_PROCESS_ID on BPEL_INSTANCE (PROCESS_ID);
CREATE INDEX IDX_BPEL_INSTANCE_STATE on BPEL_INSTANCE (STATE);
CREATE INDEX IDX_BPEL_PROCESS_TYPE_NAME on BPEL_PROCESS (type_name);
CREATE INDEX IDX_BPEL_PROCESS_TYPE_NS on BPEL_PROCESS (type_ns);
CREATE INDEX IDX_PLINK_VAL_PROCESS_IDX on BPEL_PLINK_VAL (PROCESS);
CREATE INDEX IDX_PLINK_VAL_SCOPE on BPEL_PLINK_VAL (SCOPE);
CREATE INDEX IDX_PLINK_VAL_MODELID on BPEL_PLINK_VAL (MODELID);
CREATE INDEX IDX_VARPROP_XMLDATA on VAR_PROPERTY (XML_DATA_ID);
CREATE INDEX IDX_VARPROP_NAME on VAR_PROPERTY (PROP_NAME);
CREATE INDEX IDX_VARPROP_VALUE on VAR_PROPERTY (PROP_VALUE);
CREATE INDEX IDX_MESSAGE_MEX on BPEL_MESSAGE(MEX);
CREATE INDEX IDX_MESSAGE_EXCHANGE_PIID on BPEL_MESSAGE_EXCHANGE(PIID);
CREATE INDEX IDX_SCOPE_PIID on BPEL_SCOPE(PIID);
