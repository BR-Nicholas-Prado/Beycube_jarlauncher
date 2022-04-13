#!/bin/bash
  
WF_DIR=$1
WF_JAR=$2
WF_CONFIG=$3

GUI_WS=$4

TARGET_OUTPUT=$5

[ -z $GUI_WS ] && GUI_WS=$(basename $WF_DIR)

CONSOLE_WS=$GUI_WS-dbg

[ -z $WF_CONFIG ] && WF_CONFIG=config/services.json


WF_CMD="java -jar $WF_JAR --properties $WF_CONFIG &"

#i3-msg "workspace temp"

 i3-msg "[workspace = $CONSOLE_WS] kill"
 i3-msg "[workspace = $GUI_WS] kill"

# if [ ! -z "$TARGET_OUTPUT" ]; then
#        CUR_OUTPUT=`i3-msg -t get_workspaces   | jq '.[] | select(.focused==true).output'`
#        [ $CUR_OUTPUT = "\"$TARGET_OUTPUT\"" ] || i3-msg "move workspace to output right"
#fi
#sleep 1

 i3-msg "workspace $CONSOLE_WS; append_layout /opt/br/ashpinex/windowmanager-i3/layouts/workflo-console.json"
 i3-msg "workspace $GUI_WS; append_layout /opt/br/ashpinex/windowmanager-i3/layouts/workflo-gui.json"

echo cd $WF_DIR
echo  $WF_CMD
 urxvt -cd $WF_DIR -e $WF_CMD &

