### Wireshark usage to sniff sql connection data

There is a task to find a proof about prepared statement usage by sniffing connection to Oracle 11g XE.

First of all the next filter should be used to separate database connection packets from others: 
`tcp.port eq 49161 && ip.src_host eq 127.0.0.1 && tns` due to the fact that Docker container is used to run image with Oracle 11g XE,
but it can be simply shorten to just `tns`, because this protocol is used by oracle jdbc driver and oracle db to communicate.   

Test `JdbcDogDaoTest.shouldInsertAndUpdateDogWithoutConstraints` should be used to generate traffic to oracle db.  
Oracle datasource property `maxStatements` activates prepared statement usage.  

According to the selected test there should be the next calls:
* `INSERT ...`
* `SELECT ...`
* `UPDATE ...`
* `SELECT ...`

which use prepared statement functionality.

The more precise filter `tns.data_oci.id == 0x5e || tns.data_id == 0x10 || tns.data_id == 0x6` can be used to remove uninteresting packets.  
[oracle-connection-sniffing.pcapng.gz](./oracle-connection-sniffing.pcapng.gz) - exported packets according to the filter above 
and one time test execution.
