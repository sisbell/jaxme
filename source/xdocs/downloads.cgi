#!/bin/sh
# Wrapper script around mirrors.cgi script
# (we must change to that directory in order for python to pick up the
#  python includes correctly)
# Copied from Stefan's page on mirrors
cd /www/www.apache.org/dyn/mirrors 
/www/www.apache.org/dyn/mirrors/mirrors.cgi $*