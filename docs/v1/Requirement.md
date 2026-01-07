I want to build a ETL tool to extract data from several source systems, combine them together and then load it into a target system. Below are the details.

## How many types of Source System
We will have 2 types of source systems:

1. File System: CSV file stored in Tencent COS(Cloud Object Storage), address of endpoint and bucket are provided by user in ini file

2. Database: MySQL, connection details such as host, port, database name, username, and password will be provided by the user in ini file

## How many data sources will be supported

We need to support 3 data sources:

1. XBond Marekt Quote: CSV file stored in Tencent COS, whose sub path is `/AllPriceDepth/{BUSINESS_DATE}/*.csv`

2. XBond Trade: CSV file stored in Tencent COS, whose sub path is `/XbondCfetsDeal/{BUSINESS_DATE}/*.csv`

3. Bond Future L2 Quote: MySQL table, whose table name is `bond.fut_tick`, and the filter condition is `trading_date = {BUSINESS_DATE}`

The `{BUSINESS_DATE}` is the date on which data would be extracted, which will be passed by ETL daily process mentioned later.

## How tool will be used

Tool will be built as a CLI(Command Line Interface) application. 

User should pass 2 mandatory parameters:

1. Start Date: Start date of the data to be extracted, in format of YYYYMMDD

2. End Date: End date of the data to be extracted, in format of YYYYMMDD

One additional optional parameter:

1. Config File: Path to the ini file, default value is embedded config.ini

## How tool will work

1. Check user input parameters, and throw exception if any parameter is invalid

2. Read ini file to get connection info of source systems and initialize connection

3. ETL process will be triggered day by day. Only when all ETL process of a day is done, the next day's ETL process will be triggered. The date is being processed should be passed to data source as `{BUSINESS_DATE}` parameter.


4. The ETL process on single day should follow below steps:
    1. Extract data from all source systems and execute transformation simultaneously
    2. Cache all transformed data in memory, until all data is extracted
    3. Sort all data by `receive_time` field
    4. Load data into target system by order

5. Execution progress should be printed to console.

## The type of Target System

The target system will be three temporary stream tables in DolphinDB, which are corresponding to 3 data sources mentioned above. 

These temporary stream tables should be created by our tool at start, and deleted at the end of work. The creation and deletion should be done by invocation of DolphinDB Java API to execute scripts.

More details about DolphinDB, please refer to the link: https://docs.dolphindb.cn/zh/about/ddb_intro.html

