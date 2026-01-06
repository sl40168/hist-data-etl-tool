## Plan Overview

This plan will provide technique details about how to extract csv files from COS, how to create temporary stream tables in DolphinDB, how to load data into temporary stream tables and so on.

## How to extract csv files from COS

1. Add below maven dependency information to pom.xml for COS
```xml
<dependency>
      <groupId>com.qcloud</groupId>
      <artifactId>cos_api</artifactId>
      <version>5.6.3</version>
</dependency>

```

2. Setup COS connection.

The connection information, including domain, region and bucket would be provided by ini file, with prefix "xbond". When setup connection, we will use trust key, which will be provided separated and passed in as JVM parameter while running programe.

3. Filter out all related csv files belongs to given `{BUSINESS_DATE}` with prefix as `/AllPriceDepth/{BUSINESS_DATE}/*.csv` and `/XbondCfetsDeal/{BUSINESS_DATE}/*.csv`. Please **NOTE** that the format of `{BUSINESS_DATE}` behind `/AllPriceDepth` is YYYYMMDD, while the format of `{BUSINESS_DATE}` behind `/XbondCfetsDeal` is YYYY-MM-DD.

4. Download all filtered csv files to sub folder of working directory, the structure of sub folder is `/historical/{BUSINESS_DATE}/AllPriceDepth` and `/historical/{BUSINESS_DATE}/XbondCfetsDeal`. The format of `{BUSINESS_DATE}` is YYYYMMDD here.

## How to extract data from MySQL

1. Setup MySQL connection.

The connection information, including jdbc url, username and password would be provided by ini file, with prefix "future".

2. Query data with SQL `select * from bond.fut_tick where trading_date = {BUSINESS_DATE}`. Please **NOTE** the type of trading_date colume is **int**, but not varchar.

## How to create and delete temporary stream tables in DolphinDB

1. Add below maven dependency information to pom.xml for DolphinDB
```xml
<dependency>
    <groupId>com.dolphindb</groupId>
    <artifactId>dolphindb-javaapi</artifactId>
    <version>3.00.0.2</version>
</dependency>
```
2. Setup DolphinDB connection

The connection information, including host, port, username and password would be provided by ini file, with prefix "ddb". Please refer to https://docs.dolphindb.cn/zh/javadoc/quickstart.html for more details regarding how to setup connection.

3. Create temporary stream tables in DolphinDB with below scripts, at the beginning of each ETL process

```sql
EVENT_TIME_COL='event_time'
EVENT_TIME_COL_TYPE=TIMESTAMP
RECEIVE_TIME_COL='receive_time'
RECEIVE_TIME_COL_TYPE=TIMESTAMP
CREATE_TIME_COL='create_time'
CREATE_TIME_COL_TYPE=TIMESTAMP

PARTITION_DATE_COL='business_date'
PARTITION_DATE_COL_TYPE=DATE
PARTITION_DATE_COL_SOURCE=EVENT_TIME_COL

PARTITION_SYMBOL_COL='exch_product_id'
PARTITION_SYMBOL_COL_TYPE=SYMBOL

COMMON_COL=PARTITION_DATE_COL<-PARTITION_SYMBOL_COL<-['product_type','exchange','source','settle_speed']
COMMON_COL_TYPE=[PARTITION_DATE_COL_TYPE,PARTITION_SYMBOL_COL_TYPE, SYMBOL, SYMBOL, SYMBOL, INT]

QUOTE_BIZ_COL=['level','status','pre_close_price','pre_settle_price','pre_interest','open_price','high_price','low_price','close_price','settle_price','upper_limit','lower_limit','total_volume','total_turnover','open_interest','bid_0_price','bid_0_yield','bid_0_yield_type','bid_0_tradable_volume','bid_0_volume','offer_0_price','offer_0_yield','offer_0_yield_type','offer_0_tradable_volume','offer_0_volume','bid_1_price','bid_1_yield','bid_1_yield_type','bid_1_tradable_volume','bid_1_volume','offer_1_price','offer_1_yield','offer_1_yield_type','offer_1_tradable_volume','offer_1_volume','bid_2_price','bid_2_yield','bid_2_yield_type','bid_2_tradable_volume','bid_2_volume','offer_2_price','offer_2_yield','offer_2_yield_type','offer_2_tradable_volume','offer_2_volume','bid_3_price','bid_3_yield','bid_3_yield_type','bid_3_tradable_volume','bid_3_volume','offer_3_price','offer_3_yield','offer_3_yield_type','offer_3_tradable_volume','offer_3_volume','bid_4_price','bid_4_yield','bid_4_yield_type','bid_4_tradable_volume','bid_4_volume','offer_4_price','offer_4_yield','offer_4_yield_type','offer_4_tradable_volume','offer_4_volume','bid_5_price','bid_5_yield','bid_5_yield_type','bid_5_tradable_volume','bid_5_volume','offer_5_price','offer_5_yield','offer_5_yield_type','offer_5_tradable_volume','offer_5_volume']

QUOTE_BIZ_COL_TYPE=[SYMBOL, SYMBOL, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, DOUBLE, SYMBOL, DOUBLE, DOUBLE, DOUBLE, DOUBLE, SYMBOL, DOUBLE, DOUBLE, DOUBLE, DOUBLE, SYMBOL, DOUBLE, DOUBLE, DOUBLE, DOUBLE, SYMBOL, DOUBLE, DOUBLE, DOUBLE, DOUBLE, SYMBOL, DOUBLE, DOUBLE, DOUBLE, DOUBLE, SYMBOL, DOUBLE, DOUBLE, DOUBLE, DOUBLE, SYMBOL, DOUBLE, DOUBLE, DOUBLE, DOUBLE, SYMBOL, DOUBLE, DOUBLE, DOUBLE, DOUBLE, SYMBOL, DOUBLE, DOUBLE, DOUBLE, DOUBLE, SYMBOL, DOUBLE, DOUBLE, DOUBLE, DOUBLE, SYMBOL, DOUBLE, DOUBLE, DOUBLE, DOUBLE, SYMBOL, DOUBLE, DOUBLE]

TRADE_BIZ_COL=['last_trade_price','last_trade_yield','last_trade_yield_type','last_trade_volume','last_trade_turnover','last_trade_interest','last_trade_side']
TRADE_BIZ_COL_TYPE=[DOUBLE, DOUBLE, SYMBOL, DOUBLE, DOUBLE, DOUBLE, SYMBOL]

XBOND_QUOTE_TABLE='xbond_quote'
XBOND_TRADE_TABLE='xbond_trade'
XBOND_AGGR_TABLE='xbond_market_price'
FUT_AGGR_TABLE='fut_market_price'
PRICE_TABLE='market_price'
STREAM_TABLE_SUFFIX="_stream_temp"
XBOND_QUOTE_STREAM_TABLE=XBOND_QUOTE_TABLE+STREAM_TABLE_SUFFIX
XBOND_TRADE_STREAM_TABLE=XBOND_TRADE_TABLE+STREAM_TABLE_SUFFIX
PRICE_STREAM_TABLE=PRICE_TABLE+STREAM_TABLE_SUFFIX
XBOND_AGGR_STREAM_TABLE=XBOND_AGGR_TABLE+STREAM_TABLE_SUFFIX
FUT_AGGR_STREAM_TABLE=FUT_AGGR_TABLE+STREAM_TABLE_SUFFIX

QUOTE_TABLE_COL_SUFFIX="_quote"
TRADE_TABLE_COL_SUFFIX="_trade"

FUT_REACTIVE_ENGINE_NAME=FUT_AGGR_TABLE + "_engine_temp"
FUT_REACTIVE_ENGINE_SUB_FUT_ACTION=FUT_REACTIVE_ENGINE_NAME+FUT_AGGR_STREAM_TABLE

appendSuffixForEach=def (suffix, x):x+suffix
go

PRICE_SUB_XBOND_ACTION=PRICE_TABLE+"_"+XBOND_AGGR_STREAM_TABLE
PRICE_STREAM_TABLE_TIME_COL=[EVENT_TIME_COL,RECEIVE_TIME_COL,CREATE_TIME_COL]
PRICE_STREAM_TABLE_TRADE_TIME_COL=each(appendSuffixForEach{TRADE_TABLE_COL_SUFFIX},PRICE_STREAM_TABLE_TIME_COL)
PRICE_STREAM_TABLE_TRADE_TIME_COL_TYPE=[EVENT_TIME_COL_TYPE,RECEIVE_TIME_COL_TYPE,CREATE_TIME_COL_TYPE]
PRICE_STREAM_TABLE_QUOTE_TIME_COL=each(appendSuffixForEach{QUOTE_TABLE_COL_SUFFIX},PRICE_STREAM_TABLE_TIME_COL)
PRICE_STREAM_TABLE_QUOTE_TIME_COL_TYPE=[EVENT_TIME_COL_TYPE,RECEIVE_TIME_COL_TYPE,CREATE_TIME_COL_TYPE]

TRIGGER_COL='tick_type'
TRIGGER_COL_TYPE=SYMBOL
TRIGGER_TRADE='TRADE'
TRIGGER_QUOTE='QUOTE'
TRIGGER_SNAPSHOT='SNAPSHOT'

def appendWithTimestamp(mutable table, msg)
{
    result = select *,now() from msg
    table.append!(result)
}
go

//create stream tables
QUOTE_STREAM_TABLE_COL=COMMON_COL<-QUOTE_BIZ_COL<-EVENT_TIME_COL<-RECEIVE_TIME_COL<-CREATE_TIME_COL
QUOTE_STREAM_TABLE_COL_TYPE=COMMON_COL_TYPE<-QUOTE_BIZ_COL_TYPE<-EVENT_TIME_COL_TYPE<-RECEIVE_TIME_COL_TYPE<-CREATE_TIME_COL_TYPE
QUOTE_STREAM_TABLE_INIT_CAPACITY=600000
QUOTE_STREAM_TABLE_INIT_SIZE=0

share(streamTable(QUOTE_STREAM_TABLE_INIT_CAPACITY:QUOTE_STREAM_TABLE_INIT_SIZE,QUOTE_STREAM_TABLE_COL, QUOTE_STREAM_TABLE_COL_TYPE),XBOND_QUOTE_STREAM_TABLE)
setStreamTableTimestamp(objByName(XBOND_QUOTE_STREAM_TABLE),CREATE_TIME_COL)

TRADE_STREAM_TABLE_COL=COMMON_COL<-TRADE_BIZ_COL<-EVENT_TIME_COL<-RECEIVE_TIME_COL<-CREATE_TIME_COL
TRADE_STREAM_TABLE_COL_TYPE=COMMON_COL_TYPE<-TRADE_BIZ_COL_TYPE<-EVENT_TIME_COL_TYPE<-RECEIVE_TIME_COL_TYPE<-CREATE_TIME_COL_TYPE
TRADE_STREAM_TABLE_INIT_CAPACITY=30000
TRADE_STREAM_TABLE_INIT_SIZE=0
share(streamTable(TRADE_STREAM_TABLE_INIT_CAPACITY:TRADE_STREAM_TABLE_INIT_SIZE,TRADE_STREAM_TABLE_COL,TRADE_STREAM_TABLE_COL_TYPE),XBOND_TRADE_STREAM_TABLE)
setStreamTableTimestamp(objByName(XBOND_TRADE_STREAM_TABLE),CREATE_TIME_COL)

PRICE_STREAM_TABLE_TIME_COL=[EVENT_TIME_COL,RECEIVE_TIME_COL,CREATE_TIME_COL]
PRICE_STREAM_TABLE_TRADE_TIME_COL=each(appendSuffixForEach{TRADE_TABLE_COL_SUFFIX},PRICE_STREAM_TABLE_TIME_COL)
PRICE_STREAM_TABLE_TRADE_TIME_COL_TYPE=[EVENT_TIME_COL_TYPE,RECEIVE_TIME_COL_TYPE,CREATE_TIME_COL_TYPE]
PRICE_STREAM_TABLE_QUOTE_TIME_COL=each(appendSuffixForEach{QUOTE_TABLE_COL_SUFFIX},PRICE_STREAM_TABLE_TIME_COL)
PRICE_STREAM_TABLE_QUOTE_TIME_COL_TYPE=[EVENT_TIME_COL_TYPE,RECEIVE_TIME_COL_TYPE,CREATE_TIME_COL_TYPE]

XBOND_AGGR_STREAM_TABLE_COL=COMMON_COL<-TRADE_BIZ_COL<-QUOTE_BIZ_COL<-PRICE_STREAM_TABLE_TRADE_TIME_COL<-PRICE_STREAM_TABLE_QUOTE_TIME_COL<-TRIGGER_COL<-RECEIVE_TIME_COL
XBOND_AGGR_STREAM_TABLE_COL_TYPE=COMMON_COL_TYPE<-TRADE_BIZ_COL_TYPE<-QUOTE_BIZ_COL_TYPE<-PRICE_STREAM_TABLE_TRADE_TIME_COL_TYPE<-PRICE_STREAM_TABLE_QUOTE_TIME_COL_TYPE<-TRIGGER_COL_TYPE<-RECEIVE_TIME_COL_TYPE

PRICE_STREAM_TABLE_COL=XBOND_AGGR_STREAM_TABLE_COL<-CREATE_TIME_COL
PRICE_STREAM_TABLE_COL_TYPE=XBOND_AGGR_STREAM_TABLE_COL_TYPE<-CREATE_TIME_COL_TYPE

PRICE_STREAM_TABLE_INIT_CAPACITY=QUOTE_STREAM_TABLE_INIT_CAPACITY+TRADE_STREAM_TABLE_INIT_CAPACITY
PRICE_STREAM_TABLE_INIT_SIZE=QUOTE_STREAM_TABLE_INIT_SIZE+TRADE_STREAM_TABLE_INIT_SIZE

share(streamTable(PRICE_STREAM_TABLE_INIT_CAPACITY:PRICE_STREAM_TABLE_INIT_SIZE,XBOND_AGGR_STREAM_TABLE_COL,XBOND_AGGR_STREAM_TABLE_COL_TYPE),XBOND_AGGR_STREAM_TABLE)
share(streamTable(PRICE_STREAM_TABLE_INIT_CAPACITY:PRICE_STREAM_TABLE_INIT_SIZE,PRICE_STREAM_TABLE_COL,PRICE_STREAM_TABLE_COL_TYPE),PRICE_STREAM_TABLE)


//create xbond aggregated price engine
SNAPSHOT_ENGINE_JOIN_MATCHING_COL=COMMON_COL
XBOND_AGGR_QUOTE_ENGINE_NAME='XBOND_AGGR_QUOTE_ENGINE_TEMP'
quote_trigger_metrics=[]

for(col in sqlCol(TRADE_BIZ_COL,qualifier=XBOND_TRADE_STREAM_TABLE)){
    quote_trigger_metrics.join!(col)
}
for(col in sqlCol(QUOTE_BIZ_COL,qualifier=XBOND_QUOTE_STREAM_TABLE)){
    quote_trigger_metrics.join!(col)
}
for(col in sqlCol(PRICE_STREAM_TABLE_TIME_COL,qualifier=XBOND_TRADE_STREAM_TABLE)){
    quote_trigger_metrics.join!(col)
}
for(col in sqlCol(PRICE_STREAM_TABLE_TIME_COL,qualifier=XBOND_QUOTE_STREAM_TABLE)){
    quote_trigger_metrics.join!(col)
}
quote_trigger_metrics.join!(<'QUOTE'>)
quote_trigger_metrics.join!(<xbond_quote_stream_temp.receive_time>)

xbond_aggr_quote_engine=createLookupJoinEngine(XBOND_AGGR_QUOTE_ENGINE_NAME,objByName(XBOND_QUOTE_STREAM_TABLE),objByName(XBOND_TRADE_STREAM_TABLE),objByName(XBOND_AGGR_STREAM_TABLE),metrics=quote_trigger_metrics,matchingColumn=SNAPSHOT_ENGINE_JOIN_MATCHING_COL,rightTimeColumn=RECEIVE_TIME_COL,keepDuplicates=false)

XBOND_AGGR_TRADE_ENGINE_NAME='XBOND_AGGR_TRADE_ENGINE_TEMP'
trade_trigger_metrics=[]

for(col in sqlCol(TRADE_BIZ_COL,qualifier=XBOND_TRADE_STREAM_TABLE)){
    trade_trigger_metrics.join!(col)
}
for(col in sqlCol(QUOTE_BIZ_COL,qualifier=XBOND_QUOTE_STREAM_TABLE)){
    trade_trigger_metrics.join!(col)
}
for(col in sqlCol(PRICE_STREAM_TABLE_TIME_COL,qualifier=XBOND_TRADE_STREAM_TABLE)){
    trade_trigger_metrics.join!(col)
}
for(col in sqlCol(PRICE_STREAM_TABLE_TIME_COL,qualifier=XBOND_QUOTE_STREAM_TABLE)){
    trade_trigger_metrics.join!(col)
}
trade_trigger_metrics.join!(<'TRADE'>)
trade_trigger_metrics.join!(<xbond_trade_stream_temp.receive_time>)
xbond_aggr_trade_engine=createLookupJoinEngine(XBOND_AGGR_TRADE_ENGINE_NAME,objByName(XBOND_TRADE_STREAM_TABLE),objByName(XBOND_QUOTE_STREAM_TABLE),objByName(XBOND_AGGR_STREAM_TABLE),metrics=trade_trigger_metrics,matchingColumn=SNAPSHOT_ENGINE_JOIN_MATCHING_COL,rightTimeColumn=RECEIVE_TIME_COL,keepDuplicates=false)

XBOND_AGGR_QUOTE_ENGINE_LEFT_ACTION=XBOND_AGGR_QUOTE_ENGINE_NAME+"_LEFT"
XBOND_AGGR_QUOTE_ENGINE_RIGHT_ACTION=XBOND_AGGR_QUOTE_ENGINE_NAME+"_RIGHT"
XBOND_AGGR_TRADE_ENGINE_LEFT_ACTION=XBOND_AGGR_TRADE_ENGINE_NAME+"_LEFT"
XBOND_AGGR_TRADE_ENGINE_RIGHT_ACTION=XBOND_AGGR_TRADE_ENGINE_NAME+"_RIGHT"

subscribeTable(tableName=XBOND_QUOTE_STREAM_TABLE,actionName=XBOND_AGGR_QUOTE_ENGINE_LEFT_ACTION,handler=getLeftStream(xbond_aggr_quote_engine),hash=0,msgAsTable=true,throttle=0.01,handlerNeedMsgId=true)
subscribeTable(tableName=XBOND_QUOTE_STREAM_TABLE,actionName=XBOND_AGGR_TRADE_ENGINE_RIGHT_ACTION,handler=getRightStream(xbond_aggr_trade_engine),hash=0,msgAsTable=true,throttle=0.01,handlerNeedMsgId=true)
subscribeTable(tableName=XBOND_TRADE_STREAM_TABLE,actionName=XBOND_AGGR_QUOTE_ENGINE_RIGHT_ACTION,handler=getRightStream(xbond_aggr_quote_engine),hash=2,msgAsTable=true,throttle=0.01,handlerNeedMsgId=true)
subscribeTable(tableName=XBOND_TRADE_STREAM_TABLE,actionName=XBOND_AGGR_TRADE_ENGINE_LEFT_ACTION,handler=getLeftStream(xbond_aggr_trade_engine),hash=2,msgAsTable=true,throttle=0.01,handlerNeedMsgId=true)

subscribeTable(tableName=XBOND_AGGR_STREAM_TABLE,actionName=PRICE_SUB_XBOND_ACTION,handler=appendWithTimestamp{objByName(PRICE_STREAM_TABLE)},msgAsTable=true,hash=4)

FUT_STREAM_TABLE_COL=PRICE_STREAM_TABLE_COL
FUT_STREAM_TABLE_COL_TYPE=PRICE_STREAM_TABLE_COL_TYPE
FUT_STREAM_TABLE_INIT_CAPACITY=30000
FUT_STREAM_TABLE_INIT_SIZE=0

share(streamTable(FUT_STREAM_TABLE_INIT_CAPACITY:FUT_STREAM_TABLE_INIT_SIZE,FUT_STREAM_TABLE_COL,FUT_STREAM_TABLE_COL_TYPE),FUT_AGGR_STREAM_TABLE)

fut_calc_metrics=dict(['last_trade_volume','last_trade_turnover','last_trade_interest','create_time'],[<total_volume-ifValid(prev(total_volume),0)>,<total_turnover-ifValid(prev(total_turnover),0)>,<open_interest-ifValid(prev(open_interest),0)>,<now()>])

FUT_RECEIVE_TIME_COL=RECEIVE_TIME_COL+QUOTE_TABLE_COL_SUFFIX
FUT_STREAM_TABLE_VAL_COL=TRADE_BIZ_COL<-QUOTE_BIZ_COL<-PRICE_STREAM_TABLE_TRADE_TIME_COL<-PRICE_STREAM_TABLE_QUOTE_TIME_COL<-TRIGGER_COL<-FUT_RECEIVE_TIME_COL<-CREATE_TIME_COL
fut_metric=[]
for(col in FUT_STREAM_TABLE_VAL_COL) {
    if(fut_calc_metrics[col]==NULL) {
        fut_metric.join!(sqlCol([col])[0])
    } else{
        fut_metric.join!(fut_calc_metrics[col])
    }
}

fut_reactive_engine=createReactiveStateEngine(FUT_REACTIVE_ENGINE_NAME,fut_metric,objByName(FUT_AGGR_STREAM_TABLE),objByName(PRICE_STREAM_TABLE),msgAsTable=true,keyColumn=COMMON_COL)

subscribeTable(tableName=FUT_AGGR_STREAM_TABLE,actionName=FUT_REACTIVE_ENGINE_SUB_FUT_ACTION,handler=appendMsg{fut_reactive_engine},msgAsTable=true,throttle=0.01,hash=6,handlerNeedMsgId=true)

go

XBOND_TRADE_STORAGE_ACTION='xbond_trade_storage_action_temp'
XBOND_QUOTE_STORAGE_ACTION='xbond_quote_storage_action_temp'
XBOND_PRICE_STORAGE_ACTION='xbond_price_storage_action_temp'
PRICE_STORAGE_ACTION='price_storage_action_temp'
FUT_PRICE_STORAGE_ACTION='fut_price_storage_action_temp'

QUOTE_BATCH_SIZE=10000
TRADE_BATCH_SIZE=2000
THROTTLE=1
MSG_AS_TABLE=true

OFFSET=-3
DATABASE_URL="dfs://Zing_MDS"

xbond_quote_table=loadTable(DATABASE_URL, XBOND_QUOTE_TABLE)
subscribeTable(tableName=XBOND_QUOTE_STREAM_TABLE,actionName=XBOND_QUOTE_STORAGE_ACTION,offset=OFFSET,handler=appendWithTimestamp{xbond_quote_table},msgAsTable=MSG_AS_TABLE,batchSize=QUOTE_BATCH_SIZE,throttle=THROTTLE,hash=1)

xbond_trade_table=loadTable(DATABASE_URL,XBOND_TRADE_TABLE)
subscribeTable(tableName=XBOND_TRADE_STREAM_TABLE,actionName=XBOND_TRADE_STORAGE_ACTION,offset=OFFSET,handler=appendWithTimestamp{xbond_trade_table},msgAsTable=MSG_AS_TABLE,batchSize=TRADE_BATCH_SIZE,throttle=THROTTLE,hash=3)

xbond_price_table=loadTable(DATABASE_URL,XBOND_AGGR_TABLE)
subscribeTable(tableName=XBOND_AGGR_STREAM_TABLE,actionName=XBOND_PRICE_STORAGE_ACTION,offset=OFFSET,handler=appendWithTimestamp{xbond_price_table},msgAsTable=MSG_AS_TABLE,batchSize=TRADE_BATCH_SIZE,throttle=THROTTLE,hash=5)

price_table=loadTable(DATABASE_URL,PRICE_TABLE)
subscribeTable(tableName=PRICE_STREAM_TABLE,actionName=PRICE_STORAGE_ACTION,offset=OFFSET,handler=appendWithTimestamp{price_table},msgAsTable=MSG_AS_TABLE,batchSize=QUOTE_BATCH_SIZE,throttle=THROTTLE,hash=7)

fut_price_table=loadTable(DATABASE_URL,FUT_AGGR_TABLE)
subscribeTable(tableName=FUT_AGGR_STREAM_TABLE,actionName=FUT_PRICE_STORAGE_ACTION,offset=OFFSET,handler=appendWithTimestamp{fut_price_table},msgAsTable=MSG_AS_TABLE,batchSize=QUOTE_BATCH_SIZE,throttle=THROTTLE,hash=9)
go

```

4. Clear all temporary stream tables in DolphinDB with below scripts, at the end of each ETL process


```sql
XBOND_TRADE_STORAGE_ACTION='xbond_trade_storage_action_temp'
XBOND_QUOTE_STORAGE_ACTION='xbond_quote_storage_action_temp'
XBOND_PRICE_STORAGE_ACTION='xbond_price_storage_action_temp'
PRICE_STORAGE_ACTION='price_storage_action_temp'
FUT_PRICE_STORAGE_ACTION='fut_price_storage_action_temp'

XBOND_QUOTE_TABLE='xbond_quote'
XBOND_TRADE_TABLE='xbond_trade'
XBOND_AGGR_TABLE='xbond_market_price'
FUT_AGGR_TABLE='fut_market_price'
PRICE_TABLE='market_price'
STREAM_TABLE_SUFFIX="_stream_temp"
XBOND_QUOTE_STREAM_TABLE=XBOND_QUOTE_TABLE+STREAM_TABLE_SUFFIX
XBOND_TRADE_STREAM_TABLE=XBOND_TRADE_TABLE+STREAM_TABLE_SUFFIX
PRICE_STREAM_TABLE=PRICE_TABLE+STREAM_TABLE_SUFFIX
XBOND_AGGR_STREAM_TABLE=XBOND_AGGR_TABLE+STREAM_TABLE_SUFFIX
FUT_AGGR_STREAM_TABLE=FUT_AGGR_TABLE+STREAM_TABLE_SUFFIX

unsubscribeTable(tableName=XBOND_QUOTE_STREAM_TABLE, actionName=XBOND_QUOTE_STORAGE_ACTION)
unsubscribeTable(tableName=XBOND_TRADE_STREAM_TABLE, actionName=XBOND_TRADE_STORAGE_ACTION)
unsubscribeTable(tableName=PRICE_STREAM_TABLE, actionName=PRICE_STORAGE_ACTION)
unsubscribeTable(tableName=XBOND_AGGR_STREAM_TABLE, actionName=XBOND_PRICE_STORAGE_ACTION)
unsubscribeTable(tableName=FUT_AGGR_STREAM_TABLE, actionName=FUT_PRICE_STORAGE_ACTION)

XBOND_AGGR_QUOTE_ENGINE_NAME='XBOND_AGGR_QUOTE_ENGINE_TEMP'
XBOND_AGGR_TRADE_ENGINE_NAME='XBOND_AGGR_TRADE_ENGINE_TEMP'
XBOND_AGGR_QUOTE_ENGINE_LEFT_ACTION=XBOND_AGGR_QUOTE_ENGINE_NAME+"_LEFT"
XBOND_AGGR_QUOTE_ENGINE_RIGHT_ACTION=XBOND_AGGR_QUOTE_ENGINE_NAME+"_RIGHT"
XBOND_AGGR_TRADE_ENGINE_LEFT_ACTION=XBOND_AGGR_TRADE_ENGINE_NAME+"_LEFT"
XBOND_AGGR_TRADE_ENGINE_RIGHT_ACTION=XBOND_AGGR_TRADE_ENGINE_NAME+"_RIGHT"

FUT_REACTIVE_ENGINE_NAME=FUT_AGGR_TABLE + "_engine_temp"
FUT_AGGR_STREAM_TABLE=FUT_AGGR_TABLE+STREAM_TABLE_SUFFIX
FUT_REACTIVE_ENGINE_SUB_FUT_ACTION=FUT_REACTIVE_ENGINE_NAME+FUT_AGGR_STREAM_TABLE
PRICE_SUB_XBOND_ACTION=PRICE_TABLE+"_"+XBOND_AGGR_STREAM_TABLE

unsubscribeTable(tableName=XBOND_QUOTE_STREAM_TABLE,actionName=XBOND_AGGR_QUOTE_ENGINE_LEFT_ACTION)
unsubscribeTable(tableName=XBOND_QUOTE_STREAM_TABLE,actionName=XBOND_AGGR_TRADE_ENGINE_RIGHT_ACTION)
unsubscribeTable(tableName=XBOND_TRADE_STREAM_TABLE,actionName=XBOND_AGGR_QUOTE_ENGINE_RIGHT_ACTION)
unsubscribeTable(tableName=XBOND_TRADE_STREAM_TABLE,actionName=XBOND_AGGR_TRADE_ENGINE_LEFT_ACTION)
unsubscribeTable(tableName=XBOND_AGGR_STREAM_TABLE,actionName=PRICE_SUB_XBOND_ACTION)
unsubscribeTable(tableName=FUT_AGGR_STREAM_TABLE,actionName=FUT_REACTIVE_ENGINE_SUB_FUT_ACTION)

dropStreamEngine(XBOND_AGGR_QUOTE_ENGINE_NAME)
dropStreamEngine(XBOND_AGGR_TRADE_ENGINE_NAME)
dropStreamEngine(FUT_REACTIVE_ENGINE_NAME)

dropStreamTable(XBOND_QUOTE_STREAM_TABLE)
dropStreamTable(XBOND_TRADE_STREAM_TABLE)
dropStreamTable(PRICE_STREAM_TABLE)
dropStreamTable(XBOND_AGGR_STREAM_TABLE)
dropStreamTable(FUT_AGGR_STREAM_TABLE)

```

## How to transform data of AllPriceDepth

1. The structure of AllPriceDepth source csv is as below:

id | underlying_symbol | underlying_security_id | underlying_settlement_type | underlying_md_entry_type | underlying_trade_volume | underlying_md_entry_px | underlying_md_price_level | underlying_md_entry_size | underlying_un_match_qty | underlying_yield_type | underlying_yield | transact_time | mq_partition | mq_offset | recv_time |
----|-------------------|------------------------|----------------------------|---------------------------|--------------------------|------------------------|---------------------------|--------------------------|-------------------------|----------------------|-----------------|---------------|--------------|-----------|-----------|
313852591 | - | 210210 | 2 | 0 | | 107.9197 | 1 | 10000000 | | MATURITY | 1.858 | 20260105-09:03:45.377 | 0 | 2926859 | 20260105-09:03:45.421 |
313852592 | - | 210210 | 2 | 1 | | 108.1531 | 1 | 10000000 | | MATURITY | 1.8145 | 20260105-09:03:45.377 | 0 | 2926859 | 20260105-09:03:45.421 |
313852593 | - | 210210 | 2 | 0 | | 107.9197 | 2 | 10000000 | | MATURITY | 1.858 | 20260105-09:03:45.377 | 0 | 2926859 | 20260105-09:03:45.421 |
313852594 | - | 210210 | 2 | 1 | | 108.1531 | 2 | 10000000 | | MATURITY | 1.8145 | 20260105-09:03:45.377 | 0 | 2926859 | 20260105-09:03:45.421 |

The meaning of each column is as below:
- id: The unique id of the record without any business meaning.
- 
- underlying_symbol: Always '-', ignore it.
- 
- underlying_security_id: The underlying security id of the product, considering this quote from Cfets Xbond platform, usually we add ".IB" suffix to security Id. For example, "210210" should be "210210.IB".
- 
- underlying_settlement_type: The settlement type of the underlying product which indicate the settle speed of the quote. 1 means settle speed = 0(day), and 2 means settle speed = 1(day).
- 
- underlying_md_entry_type: The side of the quote, 0 means bid, 1 means offer.
- 
- underlying_trade_volume: Always empty, ignore it.
- 
- underlying_md_entry_px: The clean price of the quote.
- 
- underlying_md_price_level: The level of the quote, from 1 to 6, in which the level 1 is the best quote in global market, but may not be tradable.
- 
- underlying_md_entry_size: The volume of the quote.
- 
- underlying_un_match_qty: Always empty, ignore it.
- 
- underlying_yield_type: The yield type of the underlying product, the possible value is MATURITY or EXERCISE(only for the bonds embedding an option).
- 
- underlying_yield: The yield of the underlying product.
- 
- transact_time: The transact time of the record, means the time when the quote is generated in market.
- 
- mq_partition: Always 0, ignore it.
- 
- mq_offset: The offset of the record. The records with the same offset **MUST** be treated as the same quote, but on different level and side.
- 
- recv_time: The receive time of the record, means the time when the record is received by our system.

2. The target structure what we want is as below:

| business_date | exch_product_id | product_type | exchange | source | settle_speed | level | status | pre_close_price | pre_settle_price | pre_interest | open_price | high_price | low_price | close_price | settle_price | upper_limit | lower_limit | total_volume | total_turnover | open_interest | bid_0_price | bid_0_yield | bid_0_yield_type | bid_0_tradable_volume | bid_0_volume | offer_0_price | offer_0_yield | offer_0_yield_type | offer_0_tradable_volume | offer_0_volume | bid_1_price | bid_1_yield | bid_1_yield_type | bid_1_tradable_volume | bid_1_volume | offer_1_price | offer_1_yield | offer_1_yield_type | offer_1_tradable_volume | offer_1_volume | bid_2_price | bid_2_yield | bid_2_yield_type | bid_2_tradable_volume | bid_2_volume | offer_2_price | offer_2_yield | offer_2_yield_type | offer_2_tradable_volume | offer_2_volume | bid_3_price | bid_3_yield | bid_3_yield_type | bid_3_tradable_volume | bid_3_volume | offer_3_price | offer_3_yield | offer_3_yield_type | offer_3_tradable_volume | offer_3_volume | bid_4_price | bid_4_yield | bid_4_yield_type | bid_4_tradable_volume | bid_4_volume | offer_4_price | offer_4_yield | offer_4_yield_type | offer_4_tradable_volume | offer_4_volume | bid_5_price | bid_5_yield | bid_5_yield_type | bid_5_tradable_volume | bid_5_volume | offer_5_price | offer_5_yield | offer_5_yield_type | offer_5_tradable_volume | offer_5_volume | event_time | receive_time | create_time |
|----------------|-----------------|--------------|----------|--------|--------------|-------|--------|-----------------|-----------------|---------------|------------|------------|-----------|-------------|--------------|------------|------------|---------------|----------------|----------------|-------------|------------|-----------------|-------------------|----------------------|---------------|--------------|---------------|-----------------|---------------------|-----------------|-------------|------------|-----------------|----------------------|---------------|--------------|---------------|-----------------|---------------------|-----------------|-------------|------------|-----------------|----------------------|---------------|--------------|---------------|-----------------|---------------------|-----------------|-------------|------------|-----------------|----------------------|---------------|--------------|---------------|-----------------|---------------------|-----------------|-------------|------------|-----------------|----------------------|---------------|--------------|---------------|-----------------|---------------------|-----------------|-------------|------------|-----------------|----------------------|---------------|--------------|-----------------|-----------------|---------------------|-----------------|-------------|------------|-----------------|----------------------|---------------|--------------|---------------|-----------------|---------------------|-----------------|-----------|-------------|-------------|
| 2026.01.06 | 092018002.IB | BOND | CFETS | XBOND | 1 | L2 | Normal | | | | | | | | | | | | 102.1069 | 1.6645 | MATURITY | | 30000000 | 102.2136 | 1.6047 | MATURITY | | 30000000 | 102.1069 | 1.6645 | MATURITY | 30000000 | | 102.2136 | 1.6047 | MATURITY | 30000000 | | 102.097 | 1.6701 | MATURITY | 30000000 | | 102.2216 | 1.6002 | MATURITY | 30000000 | | 102.069 | 1.6858 | MATURITY | 30000000 | | 102.2236 | 1.5991 | MATURITY | 30000000 | | | | | | | | 102.2602 | 1.5786 | MATURITY | 30000000 | | | | | | | | | | | | | 2026.01.06T11:01:33.300 | 2026.01.06T11:01:33.507 | 2026.01.06T11:01:33.709 |
| 2026.01.06 | 09240422.IB | BOND | CFETS | XBOND | 1 | L2 | Normal | | | | | | | | | | | | 100.1902 | 1.5444 | MATURITY | | 30000000 | 100.2182 | 1.5172 | MATURITY | | 30000000 | 100.1902 | 1.5444 | MATURITY | 30000000 | | 100.2182 | 1.5172 | MATURITY | 30000000 | | 100.1866 | 1.5479 | MATURITY | 50000000 | | 100.2205 | 1.515 | MATURITY | 30000000 | | 100.1844 | 1.55 | MATURITY | 30000000 | | 100.2218 | 1.5137 | MATURITY | 30000000 | | 100.1837 | 1.5507 | MATURITY | 30000000 | | 100.2226 | 1.5129 | MATURITY | 50000000 | | 100.1824 | 1.5519 | MATURITY | 30000000 | | 100.2247 | 1.5109 | MATURITY | 30000000 | | 2026.01.06T11:01:33.300 | 2026.01.06T11:01:33.507 | 2026.01.06T11:01:33.709 |
| 2026.01.06 | 170210.IB | BOND | CFETS | XBOND | 1 | L2 | Normal | | | | | | | | | | | | 103.9934 | 1.633 | MATURITY | | 10000000 | 104.0218 | 1.6165 | MATURITY | | 10000000 | 103.9934 | 1.633 | MATURITY | 10000000 | | 104.0218 | 1.6165 | MATURITY | 10000000 | | 103.9915 | 1.6341 | MATURITY | 10000000 | | 104.0287 | 1.6125 | MATURITY | 60000000 | | 103.9857 | 1.6375 | MATURITY | 30000000 | | 104.0362 | 1.6081 | MATURITY | 10000000 | | 103.9848 | 1.638 | MATURITY | 30000000 | | | | | | | | | | | | | | | | | 2026.01.06T11:01:33.300 | 2026.01.06T11:01:33.507 | 2026.01.06T11:01:33.709 |
| 2026.01.06 | 180205.IB | BOND | CFETS | XBOND | 1 | L2 | Normal | | | | | | | | | | | | 107.8767 | 1.6754 | MATURITY | | 10000000 | 107.9822 | 1.6349 | MATURITY | | 10000000 | 107.8767 | 1.6754 | MATURITY | 10000000 | | 107.9822 | 1.6349 | MATURITY | 10000000 | | 107.8746 | 1.6762 | MATURITY | 10000000 | | 107.9931 | 1.6307 | MATURITY | 10000000 | | 107.8676 | 1.6789 | MATURITY | 10000000 | | 107.9937 | 1.6305 | MATURITY | 10000000 | | 107.8637 | 1.6804 | MATURITY | 60000000 | | 107.9963 | 1.6295 | MATURITY | 30000000 | | | | | | | | 108.0012 | 1.6276 | MATURITY | 30000000 | | 2026.01.06T11:01:33.300 | 2026.01.06T11:01:33.507 | 2026.01.06T11:01:33.709 |
| 2026.01.06 | 190310.IB | BOND | CFETS | XBOND | 1 | L2 | Normal | | | | | | | | | | | | 107.7642 | 1.7341 | MATURITY | | 30000000 | 107.9049 | 1.6975 | MATURITY | | 30000000 | 107.7642 | 1.7341 | MATURITY | 30000000 | | 107.9049 | 1.6975 | MATURITY | 30000000 | | 107.7524 | 1.7372 | MATURITY | 30000000 | | 107.9065 | 1.6971 | MATURITY | 30000000 | | 107.7512 | 1.7375 | MATURITY | 30000000 | | 107.9145 | 1.695 | MATURITY | 10000000 | | 107.7431 | 1.7396 | MATURITY | 30000000 | | 107.9191 | 1.6938 | MATURITY | 30000000 | | 107.7224 | 1.745 | MATURITY | 30000000 | | 107.9299 | 1.691 | MATURITY | 30000000 | | 2026.01.06T11:01:33.300 | 2026.01.06T11:01:33.507 | 2026.01.06T11:01:33.709 |
| 2026.01.06 | 200204.IB | BOND | CFETS | XBOND | 1 | L2 | Normal | | | | | | | | | | | | 102.6143 | 1.603 | MATURITY | | 10000000 | 102.6373 | 1.5874 | MATURITY | | 10000000 | 102.6143 | 1.603 | MATURITY | 10000000 | | 102.6373 | 1.5874 | MATURITY | 10000000 | | 102.6114 | 1.605 | MATURITY | 30000000 | | 102.6386 | 1.5865 | MATURITY | 10000000 | | 102.6086 | 1.6069 | MATURITY | 10000000 | | 102.6395 | 1.5859 | MATURITY | 10000000 | | 102.607 | 1.608 | MATURITY | 10000000 | | 102.6414 | 1.5846 | MATURITY | 30000000 | | | | | | | | | | | | | 2026.01.06T11:01:33.300 | 2026.01.06T11:01:33.507 | 2026.01.06T11:01:33.709 |

The meaning of each column is as below:

- business_date: The trading date of the quote happened. It should come from the `{BUSINESS_DATE}` parameter while running the ETL job. Please **NOTE** its data type in DolphinDB is DATE, and you can use `date(temporalParse({BUSINESS_DATE}, 'yyyyMMdd'))` to convert it.
- 
- exch_product_id: The exchange product id of the quote, which is the security id end with ".IB". For AllPriceDepth, it comes from 'underlying_security_id'
- 
- product_type: The product type of the quote, which is always "BOND" for AllPriceDepth.
- 
- exchange: The exchange of the quote, which is always "CFETS" for AllPriceDepth.
- 
- source: The source of the quote, which is always "XBOND" for AllPriceDepth.
- 
- settle_speed: The settle speed of the quote, the possible value for AllPriceDepth is 0(day) or 1(day), based on the actual 'underlying_settlement_type' value of quote.
- 
- level: The level of the quote, which is always "L2" for AllPriceDepth.
- 
- status: The status of the quote, which is always "Normal" for AllPriceDepth.
- 
- pre_close_price: Always empty for AllPriceDepth.
- 
- pre_settle_price: Always empty for AllPriceDepth.
- 
- pre_interest: Always empty for AllPriceDepth.
- 
- open_price: Always empty for AllPriceDepth.
- 
- high_price: Always empty for AllPriceDepth.
- 
- low_price: Always empty for AllPriceDepth.
- 
- close_price: Always empty for AllPriceDepth.
- 
- settle_price: Always empty for AllPriceDepth.
- 
- upper_limit: Always empty for AllPriceDepth.
- 
- lower_limit: Always empty for AllPriceDepth.
- 
- total_volume: Always empty for AllPriceDepth.
- 
- total_turnover: Always empty for AllPriceDepth.
- 
- open_interest: Always empty for AllPriceDepth.
- 
- bid_0_price: The best bid price of the quote crossing global market, but may not be tradable.
- 
- bid_0_yield: The best bid yield of the quote crossing global market, but may not be tradable.
- 
- bid_0_yield_type: The yield type of the best bid, the possible value is MATURITY or EXERCISE(only for the bonds embedding an option).
- 
- bid_0_tradable_volume: The tradable volume of the best bid, which is always 0 for AllPriceDepth.
- 
- bid_0_volume: The volume of the best bid, because it may not be tradable, we put the volume on source quote here.
- 
- ask_0_price: The best ask price of the quote crossing global market, but may not be tradable.
- 
- ask_0_yield: The best ask yield of the quote crossing global market, but may not be tradable.
- 
- ask_0_yield_type: The yield type of the best ask, the possible value is MATURITY or EXERCISE(only for the bonds embedding an option).
- 
- ask_0_tradable_volume: The tradable volume of the best ask, which is always 0 for AllPriceDepth.
- 
- ask_0_volume: The volume of the best ask, because it may not be tradable, we put the volume on source quote here.
- 
- bid_1_price: The best bid price which is tradable for us.
- 
- bid_1_yield: The best bid yield which is tradable for us.
- 
- bid_1_yield_type: The yield type of the best tradable bid, the possible value is MATURITY or EXERCISE(only for the bonds embedding an option).
- 
- bid_1_tradable_volume: The tradable volume of the best tradable bid, because it is tradable, we put the volume on source quote here.
- 
- bid_1_volume: The volume of the best tradable bid, keep it empty for AllPriceDepth as duplicated with bid_1_tradable_volume.
- 
- ask_1_price: The best ask price which is tradable for us.
- 
- ask_1_yield: The best ask yield which is tradable for us.
- 
- ask_1_yield_type: The yield type of the best tradable ask, the possible value is MATURITY or EXERCISE(only for the bonds embedding an option).
- 
- ask_1_tradable_volume: The tradable volume of the best tradable ask, because it is tradable, we put the volume on source quote here.
- 
- ask_1_volume: The volume of the best tradable ask, keep it empty for AllPriceDepth as duplicated with ask_1_tradable_volume.
- 
- bid_2_price: The second best bid price which is tradable for us.
- 
- bid_2_yield: The second best bid yield which is tradable for us.
- 
- bid_2_yield_type: The yield type of the second best tradable bid, the possible value is MATURITY or EXERCISE(only for the bonds embedding an option).
- 
- bid_2_tradable_volume: The tradable volume of the second best tradable bid, because it is tradable, we put the volume on source quote here.
- 
- bid_2_volume: The volume of the second best tradable bid, keep it empty for AllPriceDepth as duplicated with bid_2_tradable_volume.
- 
- ask_2_price: The second best ask price which is tradable for us.
- 
- ask_2_yield: The second best ask yield which is tradable for us.
- 
- ask_2_yield_type: The yield type of the second best tradable ask, the possible value is MATURITY or EXERCISE(only for the bonds embedding an option).
- 
- ask_2_tradable_volume: The tradable volume of the second best tradable ask, because it is tradable, we put the volume on source quote here.
- 
- ask_2_volume: The volume of the second best tradable ask, keep it empty for AllPriceDepth as duplicated with ask_2_tradable_volume.
- 
- bid_3_price: The third best bid price which is tradable for us.
- 
- bid_3_yield: The third best bid yield which is tradable for us.
- 
- bid_3_yield_type: The yield type of the third best tradable bid, the possible value is MATURITY or EXERCISE(only for the bonds embedding an option).
- 
- bid_3_tradable_volume: The tradable volume of the third best tradable bid, because it is tradable, we put the volume on source quote here.
- 
- bid_3_volume: The volume of the third best tradable bid, keep it empty for AllPriceDepth as duplicated with bid_3_tradable_volume.
- 
- ask_3_price: The third best ask price which is tradable for us.
- 
- ask_3_yield: The third best ask yield which is tradable for us.
- 
- ask_3_yield_type: The yield type of the third best tradable ask, the possible value is MATURITY or EXERCISE(only for the bonds embedding an option).
- 
- ask_3_tradable_volume: The tradable volume of the third best tradable ask, because it is tradable, we put the volume on source quote here.
- 
- ask_3_volume: The volume of the third best tradable ask, keep it empty for AllPriceDepth as duplicated with ask_3_tradable_volume.
- 
- bid_4_price: The fourth best bid price which is tradable for us.
- 
- bid_4_yield: The fourth best bid yield which is tradable for us.
- 
- bid_4_yield_type: The yield type of the fourth best tradable bid, the possible value is MATURITY or EXERCISE(only for the bonds embedding an option).
- 
- bid_4_tradable_volume: The tradable volume of the fourth best tradable bid, because it is tradable, we put the volume on source quote here.
- 
- bid_4_volume: The volume of the fourth best tradable bid, keep it empty for AllPriceDepth as duplicated with bid_4_tradable_volume.
- 
- ask_4_price: The fourth best ask price which is tradable for us.
- 
- ask_4_yield: The fourth best ask yield which is tradable for us.
- 
- ask_4_yield_type: The yield type of the fourth best tradable ask, the possible value is MATURITY or EXERCISE(only for the bonds embedding an option).
- 
- ask_4_tradable_volume: The tradable volume of the fourth best tradable ask, because it is tradable, we put the volume on source quote here.
- 
- ask_4_volume: The volume of the fourth best tradable ask, keep it empty for AllPriceDepth as duplicated with ask_4_tradable_volume.
- 
- bid_5_price: The fifth best bid price which is tradable for us.
- 
- bid_5_yield: The fifth best bid yield which is tradable for us.
- 
- bid_5_yield_type: The yield type of the fifth best tradable bid, the possible value is MATURITY or EXERCISE(only for the bonds embedding an option).
- 
- bid_5_tradable_volume: The tradable volume of the fifth best tradable bid, because it is tradable, we put the volume on source quote here.
- 
- bid_5_volume: The volume of the fifth best tradable bid, keep it empty for AllPriceDepth as duplicated with bid_5_tradable_volume.
- 
- ask_5_price: The fifth best ask price which is tradable for us.
- 
- ask_5_yield: The fifth best ask yield which is tradable for us.
- 
- ask_5_yield_type: The yield type of the fifth best tradable ask, the possible value is MATURITY or EXERCISE(only for the bonds embedding an option).
- 
- ask_5_tradable_volume: The tradable volume of the fifth best tradable ask, because it is tradable, we put the volume on source quote here.
- 
- ask_5_volume: The volume of the fifth best tradable ask, keep it empty for AllPriceDepth as duplicated with ask_5_tradable_volume.
- 
- event_time: The transact_time from the source quote, means what time this quote happened.
- 
- receive_time: The receive_time from the source quote, means what time we received this quote.
- 
- create_time: It's a timestamp populated by DolphinDB automatically and means what time this record comes into the database. **NO NEED** to provide it by our programe.

3. The logic of data transformation：

3.1 Load all rows from the downloaded "AllPriceDepth" csv files base on condition of `{BUSINESS_DATE}` and group them by 'mq_offset' and 'underlying_security_id' fields, one group should be transform to one target record.

3.2 Sort the rows by 'underlying_md_price_level' and 'underlying_md_entry_type' field in ascending order.

3.3 Pick up the 'underlying_security_id', 'underlying_settlement_type', 'transact_time' and 'recv_time' fields from **THE FIRST ROW**, as the common value of this quote.

3.4 Transform 'underlying_security_id', 'underlying_settlement_type', 'transact_time' and 'recv_time' and assign to target record based on the mapping above. **NOTE** the type of 'event_time' and 'receive_time' should be timestamp. You can use below function to convert string to timestamp:
`
    temporalParse(${TIMESTAMP_STRING}, "yyyyMMdd-HH:mm:ss.SSS")
`

3.5 For each rows in group, you need to decide the level and side of quote and then assign price, yield, yield_type and volume to the corresponding fields to target record. For 'underlying_md_entry_type' field, 0 means bid and 1 means offer. And for 'underlying_md_price_level', original value 1~6 from source means the level 0~5 in target. For example, the record with 'underlying_md_entry_type' = 1 and 'underlying_md_price_level' = 2 should be mapped to 'ask_1_price', 'ask_1_yield', 'ask_1_yield_type' and 'ask_1_volume' fields.


## How to transform data of XbondCfetsDeal

1. The structure of XbondCfetsDeal source csv is as below:

id | bond_key | bond_code | symbol | deal_time | act_dt | act_tm | pre_market | trade_method | side | net_price | set_days | yield | yield_type | deal_size | recv_time | hlid |
----|----------|-----------|---------|-----------|---------|---------|------------|--------------|------|-----------|----------|-------|------------|-----------|-----------|-------------|
11568725 | 250210.IB | 250210 | 25国开10 | 2026-01-05 10:07:45.068 | 20260105 | 100745068 | 0 | 3 | Y | 98.4289 | T+1 | 1.9875 | 1 | 5000 | 2026-01-05 10:07:45.102 | 4455380029616468 |
11577382 | 250210.IB | 250210 | 25国开10 | 2026-01-05 13:57:55.352 | 20260105 | 135755352 | 0 | 3 | Y | 98.4082 | T+1 | 1.99 | 1 | 5000 | 2026-01-05 13:57:55.384 | 4455380029893492 |
11590145 | 250210.IB | 250210 | 25国开10 | 2026-01-05 15:50:54.350 | 20260105 | 155054350 | 0 | 3 | Y | 98.3668 | T+1 | 1.995 | 1 | 3000 | 2026-01-05 15:50:54.385 | 4455380030301908 |
11594821 | 250210.IB | 250210 | 25国开10 | 2026-01-05 16:42:29.981 | 20260105 | 164229981 | 0 | 3 | Y | 98.3668 | T+1 | 1.995 | 1 | 5000 | 2026-01-05 16:42:30.009 | 4455380030451540 |
11595210 | 250210.IB | 250210 | 25国开10 | 2026-01-05 16:56:02.203 | 20260105 | 165602203 | 0 | 3 | Y | 98.3668 | T+1 | 1.995 | 1 | 5000 | 2026-01-05 16:56:02.234 | 4455380030463988 |

The meaning of each field is as below:

- id: The unique id of the record, there is no business meaning.

- bond_key: The security id of the bond.

- bond_code: The code of the bond, considering we already have 'bond_key', which is end with ".IB", ignore this field.

- symbol: The short name of the bond, ignore it as well.

- deal_time: The time of the deal happened, which should be mapped to 'event_time'. The format is "yyyy-MM-dd HH:mm:ss.SSS"

- act_dt: The date of the deal happened, considering we already have 'deal_time', ignore this field

- act_tm: The time of the deal happened, considering we already have 'deal_time', ignore this field.

- pre_market: Always 0, ignore it.

- trade_method: The method of the deal, ignore it.

- side: The last trade side of the deal, which should be mapped to 'last_trade_side' with following logic: 'X'->'TKN', 'Y'->'GVN','Z'->'TRD', 'D'->'DONE'.

- net_price: The clean price of the deal, which should be mapped to 'last_trade_price'.

- set_days: The settle speed of the deal, which should be mapped to 'settle_speed'. The possible value in source is 0(day) or 1(day).

- yield: The yield of the deal, which should be mapped to 'last_trade_yield'.

- yield_type: The yield type of the deal, which should be mapped to 'last_trade_yield_type'. The possible value in source is 0(MATURITY) or 1(EXERCISE)

- deal_size: The volume of the deal, which should be mapped to 'last_trade_volume'.

- recv_time: The timestamp means what time we received this record, which should be mapped to 'receive_time'. The format is "yyyy-MM-dd HH:mm:ss.SSS" and **NOTE** as this field was added after the first version, it's not mandatory for us.

- hlid: The unique id of the deal, ignore it.

2. The structure of target is as below:

business_date | exch_product_id | product_type | exchange | source | settle_speed | last_trade_price | last_trade_yield | last_trade_yield_type | last_trade_volume | last_trade_turnover | last_trade_interest | last_trade_side | event_time | receive_time | create_time |
---------------|-----------------|--------------|----------|--------|--------------|------------------|-----------------|-----------------------|-------------------|---------------------|---------------------|-----------------|------------|--------------|-------------|
2026.01.06 | 250210.IB | BOND | CFETS | XBOND | 1 | 99.912 | 1.8096 | MATURITY | 10000000 | | | TKN | 2026.01.06T11:01:34.079 | 2026.01.06T11:01:34.246 | 2026.01.06T11:01:34.255 |
2026.01.06 | 230023.IB | BOND | CFETS | XBOND | 1 | 122.8227 | 1.945 | MATURITY | 20000000 | | | TKN | 2026.01.06T11:01:36.085 | 2026.01.06T11:01:36.255 | 2026.01.06T11:01:36.258 |
2026.01.06 | 230023.IB | BOND | CFETS | XBOND | 1 | 122.8227 | 1.945 | MATURITY | 20000000 | | | TKN | 2026.01.06T11:01:36.085 | 2026.01.06T11:01:36.258 | 2026.01.06T11:01:36.262 |
2026.01.06 | 250210.IB | BOND | CFETS | XBOND | 1 | 99.9101 | 1.8098 | MATURITY | 20000000 | | | GVN | 2026.01.06T11:01:36.689 | 2026.01.06T11:01:36.860 | 2026.01.06T11:01:36.863 |
2026.01.06 | 250210.IB | BOND | CFETS | XBOND | 1 | 99.9108 | 1.8097 | MATURITY | 10000000 | | | GVN | 2026.01.06T11:01:41.867 | 2026.01.06T11:01:42.045 | 2026.01.06T11:01:42.049 |
2026.01.06 | 250410.IB | BOND | CFETS | XBOND | 1 | 98.3626 | 1.85 | MATURITY | 30000000 | | | TKN | 2026.01.06T11:01:45.120 | 2026.01.06T11:01:45.307 | 2026.01.06T11:01:45.310 |

The meaning of each field is as below:

- business_date: The business date of the deal, which is the same as the 'business_date' in AllPriceDepth.

- exch_product_id: The security id of the bond, which is from 'bond_key' field in source.

- product_type: The product type of the bond, which is always 'BOND'.

- exchange: The exchange of the bond, which is always 'CFETS'.

- source: The source of the deal, which is always 'XBOND'.

- settle_speed: The settle speed of the deal, which is from 'set_days' field in source. The possible value in source is 0(day) or 1(day), and no need additional processing.

- last_trade_price: The clean price of the deal, which is from 'net_price' field in source.

- last_trade_yield: The yield of the deal, which is from 'yield' field in source.

- last_trade_yield_type: The yield type of the deal, which is from 'yield_type' field in source. The possible value in source is 0(MATURITY) or 1(EXERCISE), and no need additional processing.

- last_trade_volume: The volume of the deal, which is from 'deal_size' field in source.

- last_trade_turnover: The turnover of the deal, which is always empty.

- last_trade_interest: The interest of the deal, which is always empty.

- last_trade_side: The last trade side of the deal, which is from 'side' field in source. The possible value in source is 'X', 'Y', 'Z' and 'D', and need to be mapped to 'TKN', 'GVN', 'TRD' and 'DONE'.

- event_time: The time of the deal happened, which is from 'deal_time' field in source. The format is "yyyy-MM-dd HH:mm:ss.SSS" and you can use `temporalParse` function to convert it from string to timestamp.

- receive_time: The timestamp means what time we received this record, which is from 'recv_time' field in source. The format is "yyyy-MM-dd HH:mm:ss.SSS" and **NOTE** as this field was added after the first version, it's not mandatory for us. If the field does not exist on source record, use 'event_time' fill it. You can use `temporalParse` function to convert it from string to timestamp.

- create_time: It's a timestamp populated by DolphinDB automatically and means what time this record comes into the database. **NO NEED** to provide it by our programe.

3. The logic of data transformation：

There is no complex logic in data transformation, but just follow mentioned mapping rules above and assign the value to target record.

## How to transform data of Mysql fut_tick table

1. The structure of MySQL fut_tick source table is as below:

| id | exchg | code | price | open | high | low | settle_price | upper_limit | lower_limit | total_volume | volume | total_turnover | turn_over | open_interest | diff_interest | trading_date | action_date | action_time | pre_close | pre_settle | pre_interest | bid_prices | ask_prices | bid_qty | ask_qty | receive_time |
----|-------|------|-------|------|------|-----|--------------|------------|-------------|--------------|--------|----------------|-----------|---------------|--------------|-------------|-------------|-------------|-----------|------------|--------------|------------|----------|---------|----------|-------------|
127991794 | CFFEX | T2503 | 109.025 | 109.02 | 109.075 | 109.02 | 0 | 111.085 | 106.735 | 207 | 0 | 225676000 | 0 | 167909 | 0 | 20250102 | 20250102 | 93000400 | 108.925 | 108.91 | 167863 | [109.025, 0, 0, 0, 0] | [109.045, 0, 0, 0, 0] | [52, 0, 0, 0, 0] | [3, 0, 0, 0, 0] | |
127991795 | CFFEX | T2506 | 108.98 | 108.95 | 108.98 | 108.95 | 0 | 110.985 | 106.635 | 11 | 0 | 11985600 | 0 | 20491 | 0 | 20250102 | 20250102 | 93000400 | 108.84 | 108.81 | 20492 | [108.98, 0, 0, 0, 0] | [108.995, 0, 0, 0, 0] | [3, 0, 0, 0, 0] | [3, 0, 0, 0, 0] | |
127991796 | CFFEX | T2509 | 108.7 | 0 | 0 | 0 | 0 | 0 | 110.85 | 106.51 | 0 | 0 | 0 | 2299 | 0 | 20250102 | 20250102 | 93000400 | 108.7 | 108.68 | 2299 | [108.78, 0, 0, 0, 0] | [108.825, 0, 0, 0, 0] | [6, 0, 0, 0, 0] | [2, 0, 0, 0, 0] | |
127991797 | CFFEX | TF2503 | 106.595 | 106.6 | 106.6 | 106.59 | 0 | 107.78 | 105.23 | 167 | 0 | 178021000 | 0 | 121807 | 0 | 20250102 | 20250102 | 93000400 | 106.54 | 106.505 | 121874 | [106.59, 0, 0, 0, 0] | [106.595, 0, 0, 0, 0] | [1, 0, 0, 0, 0] | [4, 0, 0, 0, 0] | |

The meaning of each field is as below:

- id: The unique id of the record, which is not business meaning.

- exchg: The exchange of the bond future. It always is 'CFFEX' in this source.

- code: The security id of the quote, which is the same as the 'exch_product_id' in AllPriceDepth.

- price: The last trade price of the quoted security, which is the same as the 'last_trade_price' in XbondCfetsDeal.

- open: The open price of the quoted security today.

- high: The highest price of the quoted security today.

- low: The lowest price of the quoted security today.

- settle_price: The settle price of the quoted security today. In trading time, it's always 0.

- upper_limit: The upper limit price of the quoted security today.

- lower_limit: The lower limit price of the quoted security today.

- total_volume: The total deal volume of the quoted security today in market, the unit is lot.

- volume: It's always 0 in this source, ignore it.

- total_turnover: The total deal turnover of the quoted security today in market, the unit is yuan.

- turn_over: It's always 0 in this source, ignore it.

- open_interest: The open interest of the quoted security today, the unit is lot.

- diff_interest: It's always 0 in this source, ignore it.

- trading_date: The trading date of the quote, ignore it during transformation.

- action_date: The action date of the quote, ignore it during transformation.

- action_time: The action time of the quote, which is the same as the 'event_time' in XbondCfetsDeal. But when convert to 'event_time', need to combine with 'action_date' field together as a complete timestamp source. **NOTE** the value of this field is a number, whose value is calculated by `HOUR * 10000000 + MINUTE * 100000 + SECOND * 1000 + MILLISECOND`.

- pre_close: The close price of the quoted security yesterday.

- pre_settle: The settle price of the quoted security yesterday.

- pre_interest: The open interest of the quoted security yesterday, the unit is lot.

- bid_prices: The bid price of the quoted security today, the unit is yuan. **NOTE** the value of this field is a vector, whose value is calculated by `[bid_1_price, bid_2_price, bid_3_price, bid_4_price, bid _5_price]`, but as our source is 'L1', only 'bid_1_price' has valid value, others are all 0.

- ask_prices: The ask price of the quoted security today, the unit is yuan. **NOTE** the value of this field is a vector, whose value is calculated by `[ask_1_price, ask_2_price, ask_3_price, ask_4_price, ask _5_price]`, but as our source is 'L1', only 'ask_1_price' has valid value, others are all 0.

- bid_qty: The bid volume of the quoted security today, the unit is lot. **NOTE** the value of this field is a vector, whose value is calculated by `[bid_1_qty, bid_2_qty, bid_3_qty, bid_4_qty, bid _5_qty]`, but as our source is 'L1', only 'bid_1_qty' has valid value, others are all 0. The unit is lot.

- ask_qty: The ask volume of the quoted security today, the unit is lot. **NOTE** the value of this field is a vector, whose value is calculated by `[ask_1_qty, ask_2_qty, ask_3_qty, ask_4_qty, ask _5_qty]`, but as our source is 'L1', only 'ask_1_qty' has valid value, others are all 0. The unit is lot.

- receive_time: The timestamp means what time we received this record. The format is "yyyyMMdd HHmmss.SSS" and **NOTE** as this field was added after the first version, it's not mandatory for us. 

2. The structure of target is as below:

| business_date | exch_product_id | product_type | exchange | source | settle_speed | last_trade_price | last_trade_yield | last_trade_yield_type | last_trade_volume | last_trade_turnover | last_trade_interest | last_trade_side | level | status | pre_close_price | pre_settle_price | pre_interest | open_price | high_price | low_price | close_price | settle_price | upper_limit | lower_limit | total_volume | total_turnover | open_interest | bid_0_price | bid_0_yield | bid_0_yield_type | bid_0_tradable_volume | bid_0_volume | offer_0_price | offer_0_yield | offer_0_yield_type | offer_0_tradable_volume | offer_0_volume | bid_1_price | bid_1_yield | bid_1_yield_type | bid_1_tradable_volume | bid_1_volume | offer_1_price | offer_1_yield | offer_1_yield_type | offer_1_tradable_volume | offer_1_volume | bid_2_price | bid_2_yield | bid_2_yield_type | bid_2_tradable_volume | bid_2_volume | offer_2_price | offer_2_yield | offer_2_yield_type | offer_2_tradable_volume | offer_2_volume | bid_3_price | bid_3_yield | bid_3_yield_type | bid_3_tradable_volume | bid_3_volume | offer_3_price | offer_3_yield | offer_3_yield_type | offer_3_tradable_volume | offer_3_volume | bid_4_price | bid_4_yield | bid_4_yield_type | bid_4_tradable_volume | bid_4_volume | offer_4_price | offer_4_yield | offer_4_yield_type | offer_4_tradable_volume | offer_4_volume | bid_5_price | bid_5_yield | bid_5_yield_type | bid_5_tradable_volume | bid_5_volume | offer_5_price | offer_5_yield | offer_5_yield_type | offer_5_tradable_volume | offer_5_volume | event_time_trade | receive_time_trade | create_time_trade | event_time_quote | receive_time_quote | create_time_quote | tick_type | receive_time | create_time |
|:-------------|:----------------|:-------------|:---------|:-------|:-------------|:------------------|:----------------|:---------------------|:-------------------|:---------------------|:-----------------|:-----|:-------|:-----------------|:-----------------|:-------------|:-----------|:-----------|:----------|:------------|:-------------|:------------|:-------------|:--------------|:----------------|:-------------|:------------|:----------------|:-------------------|:----------------------|:---------------|:--------------|:---------------|:-----------------|:---------------------|:-----------------|:-------------|:------------|:-----------------|:----------------------|:---------------|:--------------|:---------------|:-----------------|:---------------------|:-----------------|:-------------|:------------|:-----------------|:----------------------|:---------------|:--------------|:---------------|:-----------------|:---------------------|:-----------------|:-------------|:------------|:-----------------|:----------------------|:---------------|:--------------|:---------------|:-----------------|:---------------------|:-----------------|:-------------|:------------|:-----------------|:----------------------|:---------------|:--------------|:---------------|:-----------------|:---------------------|:-----------------|:-------------|:------------|:-----------------|:----------------------|:---------------|:--------------|:-----------------|:-----------------|:---------------------|:-----------------|:-------------|:-----------------|:-----------------|
| 2026.01.06 | TS2512 | BOND_FUT | CFFEX | CFFEX | 0 | 102.471999999999994 | | | | | | | L1 | Normal | 102.468000000000003 | 102.465999999999993 | 67365 | 102.475999999999999 | 102.481999999999999 | 102.469999999999998 | | 0 | 102.977999999999994 | 101.953999999999993 | 9810 | 2.01059E10 | 66148 | 102.471999999999994 | | | 144 | | 102.474000000000003 | | | 125 | | 102.471999999999994 | | | 144 | | 102.474000000000003 | | | 125 | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | 2026.01.06T11:01:36.300 | 2026.01.06T11:01:36.469 | 2026.01.06T11:01:36.785 | 2026.01.06T11:01:36.300 | 2026.01.06T11:01:36.469 | 2026.01.06T11:01:36.785 | SNAPSHOT | 2026.01.06T11:01:36.469 | |
| 2026.01.06 | TS2603 | BOND_FUT | CFFEX | CFFEX | 0 | 102.424000000000006 | | | | | | | L1 | Normal | 102.415999999999996 | 102.415999999999996 | 16441 | 102.430000000000006 | 102.433999999999997 | 102.418000000000006 | | 0 | 102.927999999999997 | 101.903999999999996 | 3035 | 6.217190000000001E9 | 16813 | 102.421999999999997 | | | 39 | | 102.424000000000006 | | | 20 | | 102.421999999999997 | | | 39 | | 102.424000000000006 | | | 20 | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | 2026.01.06T11:01:36.300 | 2026.01.06T11:01:36.469 | 2026.01.06T11:01:36.787 | 2026.01.06T11:01:36.300 | 2026.01.06T11:01:36.469 | 2026.01.06T11:01:36.787 | SNAPSHOT | 2026.01.06T11:01:36.469 | |
| 2026.01.06 | T2512 | BOND_FUT | CFFEX | CFFEX | 0 | 108.519999999999995 | | | | | | | L1 | Normal | 108.484999999999999 | 108.484999999999999 | 231393 | 108.510000000000005 | 108.590000000000003 | 108.5 | | 0 | 110.650000000000005 | 106.319999999999993 | 24922 | 2.70525E10 | 230601 | 108.519999999999995 | | | 123 | | 108.525000000000005 | | | 61 | | 108.519999999999995 | | | 123 | | 108.525000000000005 | | | 61 | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 2026.01.06T11:01:36.800 | 2026.01.06T11:01:36.954 | 2026.01.06T11:01:37.259 | 2026.01.06T11:01:36.800 | 2026.01.06T11:01:36.954 | 2026.01.06T11:01:37.259 | SNAPSHOT | 2026.01.06T11:01:36.954 | |
| 2026.01.06 | T2603 | BOND_FUT | CFFEX | CFFEX | 0 | 108.290000000000006 | | | | | | | L1 | Normal | 108.260000000000005 | 108.25 | 58831 | 108.275000000000005 | 108.329999999999998 | 108.260000000000005 | | 0 | 110.415000000000006 | 106.084999999999993 | 5713 | 6.18739E9 | 59663 | 108.284999999999996 | | | 55 | | 108.290000000000006 | | | 16 | | 108.284999999999996 | | | 55 | | 108.290000000000006 | | | 16 | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 0 | | | 2026.01.06T11:01:36.800 | 2026.01.06T11:01:36.955 | 2026.01.06T11:01:37.263 | 2026.01.06T11:01:36.800 | 2026.01.06T11:01:36.955 | 2026.01.06T11:01:37.263 | SNAPSHOT | 2026.01.06T11:01:36.955 | |

---

The meaning of each field is as below:

- business_date: The business date of the quote, which is the same as the 'business_date' in AllPriceDepth.

- exch_product_id: The security id of the quote, which is source from 'code' field.

- product_type: The product type of the quote, which is always 'BOND_FUT' in this source.

- exchange: The exchange of the bond future, which is always 'CFFEX' in this source.

- source: The source of the quote, which is always 'CFFEX' in this source.

- settle_speed: The settle speed of the quote, which is always 0 in this source.

- last_trade_price: The last trade price of the quote, which is source from 'price' field.

- last_trade_yield: The last trade yield of the quote, which is always null in this source.

- last_trade_yield_type: The last trade yield type of the quote, which is always null in this source.

- last_trade_volume: This would be calculated by stream engine and keep it null here.

- last_trade_turnover: This would be calculated by stream engine and keep it null here.

- last_trade_interest: This would be calculated by stream engine and keep it null here.

- last_trade_side: This is aggregated quote with out trade detail, so keep it null here.

- level: The level of the quote, which is always 'L1' in this source.

- status: The status of the quote, which is always 'Normal' in this source.

- pre_close_price: The close price of the quoted security yesterday, which is source from 'pre_close' field.

- pre_settle_price: The settle price of the quoted security yesterday, which is source from 'pre_settle' field.

- pre_interest: The open interest of the quoted security yesterday, which is source from 'pre_interest' field.

- open_price: The open price of the quoted security today, which is source from 'open' field.

- high_price: The high price of the quoted security today, which is source from 'high' field.

- low_price: The low price of the quoted security today, which is source from 'low' field.

- close_price: The close price of the quoted security today, but during the day, it's always null.

- settle_price: The settle price of the quoted security today, which is source from 'settle_price' field.

- upper_limit: The upper limit price of the quoted security today, which is source from 'upper_limit' field.

- lower_limit: The lower limit price of the quoted security today, which is source from 'lower_limit' field.

- total_volume: The total volume of the quoted security today, which is source from 'total_volume' field.

- total_turnover: The total turnover of the quoted security today, which is source from 'total_turnover' field.

- open_interest: The open interest of the quoted security today, which is source from 'open_interest' field.

- bid_0_price: As all quotes are tradable in Bond Future market, keep null here.

- bid_0_yield: Meaningless for Bond Future, keey null here.

- bid_0_yield_type: Meaningless for Bond Future, keey null here.

- bid_0_tradable_volume: As all quotes are tradable in Bond Future market, keey null here.

- bid_0_volume: As all quotes are tradable in Bond Future market, keey null here.

- offer_0_price: As all quotes are tradable in Bond Future market, keey null here.

- offer_0_yield: Meaningless for Bond Future, keey null here.

- offer_0_yield_type: Meaningless for Bond Future, keey null here.

- offer_0_tradable_volume: As all quotes are tradable in Bond Future market, keey null here.

- offer_0_volume: As all quotes are tradable in Bond Future market, keey null here.

- bid_1_price: The best tradable bid price for us, which is source from 'bid_prices' field and pick up the first one.

- bid_1_yield: Meaningless for Bond Future, keey null here.

- bid_1_yield_type: Meaningless for Bond Future, keey null here.

- bid_1_tradable_volume: The best tradable bid volume for us, which is source from 'bid_qty' field and pick up the first one.

- bid_1_volume: Keep it null as we already have 'bid_1_tradable_volume'.

- offer_1_price: The best tradable offer price for us, which is source from 'ask_prices' field and pick up the first one.

- offer_1_yield: Meaningless for Bond Future, keey null here.

- offer_1_yield_type: Meaningless for Bond Future, keey null here.

- offer_1_tradable_volume: The best tradable offer volume for us, which is source from 'ask_qty' field and pick up the first one.

- offer_1_volume: Keep it null as we already have 'offer_1_tradable_volume'.

- bid_2_price ~ bid_5_price: L1 quote does not contains deep levels, so keep them null here.

- offer_2_price ~ offer_5_price: L1 quote does not contains deep levels, so keep them null here.

- bid_2_yield ~ bid_5_yield: Meaningless for Bond Future, keey null here.

- offer_2_yield ~ offer_5_yield: Meaningless for Bond Future, keey null here.

- bid_2_yield_type ~ bid_5_yield_type: Meaningless for Bond Future, keey null here.

- offer_2_yield_type ~ offer_5_yield_type: Meaningless for Bond Future, keey null here.

- bid_2_tradable_volume ~ bid_5_tradable_volume: L1 quote does not contains deep levels, so keep them null here.

- offer_2_tradable_volume ~ offer_5_tradable_volume: L1 quote does not contains deep levels, so keep them null here.

- bid_2_volume ~ bid_5_volume: Keep it null as we already have 'bid_2_tradable_volume ~ bid_5_tradable_volume'.

- offer_2_volume ~ offer_5_volume: Keep it null as we already have 'offer_2_tradable_volume ~ offer_5_tradable_volume'.

- event_time_trade & event_time_quote: The event time of the quote, which is source from the combination of 'action_date' and 'action_time' field. Please **NOTE** both 'action_date' and 'action_time' fields are in number type, so we need to convert them to string type firstly.

- receive_time_trade & receive_time_quote: The timestamp when this quote arrives at our system. It should source from 'receive_time', but if it's empty or does not exist, use the value from 'event_time_trade' instead.

- tick_type: The tick type of the quote, which is always 'SNAPSHOT' in this source.

- create_time_trade & create_time_quote: The timestamp when this quote is created. Generate it on your local in programe.

- create_time: Keep it null in this source.