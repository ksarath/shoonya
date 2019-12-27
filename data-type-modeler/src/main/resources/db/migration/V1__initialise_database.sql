
-- Application Entity
CREATE TABLE IF NOT EXISTS application_entity (
   id uuid NOT NULL,
   application_id uuid NOT NULL,
   name varchar(128) ,
   entity json,
   CONSTRAINT application_entity_primary_key PRIMARY KEY (id),
   CONSTRAINT application_entity_name_unique_key UNIQUE KEY (application_id, name)
);
