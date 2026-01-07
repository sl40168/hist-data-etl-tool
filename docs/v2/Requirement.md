
This is a new version with some enhancements to historical etl tool with purpose to make it much easier to use and validate data.

## R1: Modify CosExtractor.java to store source files on local.

Split the CosExtractor.extract to 2 separated steps:

1. Filter out all needed source files based on condition, and then download each file to local. The target folder is below current working directory with sub folder structure as `{BUSINESS_DATE}\{SOURCE_NAME}\`, where `{BUSINESS_DATE}` is the date on which data would be extracted, and `{SOURCE_NAME}` is the name of source system, such as `AllPriceDepth` or `XbondCfetsDeal`. For example, `AllPriceDepth/20250728/200310.csv` should be saved as `20250728/AllPriceDepth/200310.csv`. 

With this step, we can check and compare source data easily.

2. Once all files are downloaded, you need to load them back one by one and combine all records together, then return to next step.
**NOTE** you should load the files below the certain folder based on current business date and the source you are extracting. If you are handling XbondCfetsDeal on 20250728, then you should only load files under `20250728/XbondCfetsDeal/`.

3. Before EtlCli starts working, check if there is any downloaded source file under current working directory. If yes, give a warning and ask user whether to delete them. If no, then continue. This action would avoid the source files downloaded in other execution pollute the current execution.

4. Each step should be able to execute manually. Considering COS is an external system and the function can not be covered in unit test, we need a way to do integration test with external system manually and validate the results.

## R2: In DolphinDbLoader, load scripts to createTemporaryTables and cleanup temporary tables from local file

1. Considering the DolphinDB is an external system, it's not a good idea to hardcode the scripts in Java Class, as we can not test these scripts against DolphinDB easily. So we should setup 2 separated script files for both create and cleanup temporary stream tables, and then load them from local file. Thus we can test the script files separately against DolphinDB.

2. The script file to create temporary stream table is in `/docs/v2/create_stream_tables.dos`, **USE THIS** directly as the script to create temporary stream tables.

3. The script file to cleanup temporary stream table is in `/docs/v2/cleanup_stream_tables.dos`, **USE THIS** directly as the script to cleanup temporary stream tables.

## R3: Build ORM framework to load data into DolphinDB

The data loading API of DolphinDB is column based, which is different to the row based API, such as MySQL. so we need to build an ORM framework to make it easier to load data into DolphinDB.

1. When generating the business domain model, we should add an annotation to specify the column name of the field in DolphinDB.

2. When generating the business domain model, the type of each field **MUST** be compatible with DolphinDB. For example, `SYMBOL` in DolphinDB should be mapped to `string`, `int` should be mapped to `int`, etc.

3. Table definition of DolphinDB will be provided in `task` phase.

## R4: Add a speed throttle to DolphinDBLoader while loading data 

While loading data into DolphinDB, we need to add a speed throttle to avoid the load speed exceeding the limit of DolphinDB. The default value is 100 records/second, but it should be configurable in INI config file.

## R5: Validate data loading result on each day complete
Need to compare the total number of records loaded in DolphinDB with the number of source rows extract from Source System. Both numbers should be the same.

## R6: Make each component can be run independently for integration test

1. Components include the concrete classes of CosExtractor and DolphinDbLoader.

2. Each component should have a main method to allow standalone execution.

3. Provide configuration options to specify input/output paths and other parameters.

4. Document how to run each component independently for testing purposes.

