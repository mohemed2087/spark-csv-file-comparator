# spark-csv-file-comparator
Technologies used : Spark,Spark SQL,DataFrames,Scala,SBT etc..

Description: A utility developed in Spark for comparing two CSV files with same schema having millions or billions of rows or records in it.

Usage :

spark-submit <master file location><location of file to compare><Output location>

In this version, The final comparison or match summary can be viewed in text file which is generated in the output location with below format.

MATCH COUNT :
NON MATCH COUNT:
MATCH PERCENTAGE % :
