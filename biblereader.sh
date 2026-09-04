#!/bin/bash

# Move into the Bible Reader project directory.
# This is important because BibleReader reads db.properties
# from the current directory.
cd "/home/u-gene/bibleyear" || exit 1

# Start the Chronological Bible Reader.
# JavaFX provides the GUI and WebView.
# MySQL Connector/J provides the database connection for journal entries.
exec java \
    --module-path /usr/share/openjfx/lib \
    --add-modules javafx.controls,javafx.web \
    -cp "src:lib/mysql-connector-j-26.7.0/mysql-connector-j-26.7.0.jar" \
    BibleReader
