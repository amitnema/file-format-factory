
[![Build Status](https://travis-ci.org/amitnema/file-format-factory.svg?branch=master)](https://travis-ci.org/amitnema/file-format-factory)

# File Format Factory

## Converts different file formats
This is an api useful to convert the different version of files. Currently supported version are json to dsv (delimiter separated values) and vice-versa.

## Build the Source
Run the following command in the workspace

	mvn clean package

## Convert to the file formats
	java -jar path\to\jar\file-format-factory-{version}-jar-with-dependencies.jar <input_filepath> <input_file_type> <output_filepath> <output_file_type> <delimiter> 

e.g.

	java -jar file-format-factory-1.0.0-jar-with-dependencies.jar /tmp/data/data.json json /tmp/data/data.csv dsv ,

Above command will convert the json file into the CSV format.

#### File Types
*   dsv
*   json


Note: delimiter argument is required, if one of the file type is dsv (delimited separated values).
