# Diagnostic Code Lookup Table Updater

## Overview

The `dc_lookup_table_updater.py` script is used to update the Diagnostic Code Lookup Table in the database. The script reads a CSV file containing the new data
and updates the database with the new data. The script also logs the changes made to the database.

# Using the dc_lookup_table_updater.py script

1. Prepare the new data CSV file and add it to this directory. The CSV file must have the following columns:
    * Diagnostic Code **(Required)**
    * Rated Issue Name
    * Max Rating
    * Body System
    * Category
    * Subcategory
    * CFR Reference
      Preparation includes removing any extra punctuation in the `Rated Issue Name` column.

2. Run the script with the following command from the directory `domain-ee/ee-max-cfi-app/scripts`
    ```
    python dc_lookup_table_updater.py <new_data_csv_file>.csv
    ```
