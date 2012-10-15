    <f11:page height="780" name="TRND0003" value="/images/base/back.png" width="1120">
        <f11:trendgraph2 background="navy" foreground="white"
            height="780" width="1028" x="0" y="0" horizontalScaleFile="/resources/HorizontalScaleButtonFactory.xml">
            <f11:graphmodel class="org.F11.scada.applet.graph.DefaultSelectiveGraphModel">
                <f11:handler>
                    <f11:name value="log_table_minute_new"/>
                    <f11:name value="log_table_hour"/>
                    <f11:name value="log_table_daily"/>
                </f11:handler>
            </f11:graphmodel>
            <f11:graphproperty horizontalPixcelWidth="800"
                horizontalScaleCount="8" horizontalScaleWidth="3600000"
                scaleOneHeightPixel="5" verticalScaleCount="10" verticalScaleHeight="45">
                <f11:insets botom="50" left="10" right="10" top="60"/>
                <f11:graphview botom="50" left="30" right="30" top="60"/>
                <f11:color>
                    <f11:name value="yellow"/>
                    <f11:name value="red"/>
                    <f11:name value="magenta"/>
                    <f11:name value="lime"/>
                    <f11:name value="cyan"/>
                    <f11:name value="white"/>
                </f11:color>
                <f11:handler>
                    <f11:name value="log_table_minute_new"/>
                    <f11:name value="log_table_minute_new"/>
                    <f11:name value="log_table_hour"/>
                    <f11:name value="log_table_hour"/>
                    <f11:name value="log_table_daily"/>
                    <f11:name value="log_table_daily"/>
                </f11:handler>
                <f11:explanatory font_style="PLAIN"/>
                <f11:vertically backGroundColor="navy"
                    foregroundColor1="" foregroundColor2=""/>
                <f11:series name="温度・湿度" size="3">
                    <f11:property name="minimums">
                        <f11:element value="0.0"/>
                        <f11:element value="0.0"/>
                        <f11:element value="0.0"/>
                    </f11:property>
                    <f11:property name="maximums">
                        <f11:element value="100.0"/>
                        <f11:element value="100.0"/>
                        <f11:element value="100.0"/>
                    </f11:property>
                    <f11:property name="inputminimums">
                        <f11:element value="0.0"/>
                        <f11:element value="0.0"/>
                        <f11:element value="0.0"/>
                    </f11:property>
                    <f11:property name="inputmaximums">
                        <f11:element value="4000.0"/>
                        <f11:element value="4000.0"/>
                        <f11:element value="4000.0"/>
                    </f11:property>
                    <f11:holder name="holder">
                        <f11:name value="P1_D_500_BcdSingle"/>
                        <f11:name value="P1_D_501_BcdSingle"/>
                        <f11:name value="P1_D_502_BcdSingle"/>
                    </f11:holder>
                    <f11:property name="pointname">
                        <f11:name value="$(0_unit) $(0_name)"/>
                        <f11:name value="$(1_unit) $(1_name)"/>
                        <f11:name value="$(38_unit) $(38_name)"/>
                    </f11:property>
                    <f11:property name="unitmark">
                        <f11:name value="$(0_unit_mark)"/>
                        <f11:name value="$(1_unit_mark)"/>
                        <f11:name value="$(38_unit_mark)"/>
                    </f11:property>
                </f11:series>
                <f11:series name="てすと" size="3">
                    <f11:property name="minimums">
                        <f11:element value="0.0"/>
                        <f11:element value="0.0"/>
                        <f11:element value="0.0"/>
                    </f11:property>
                    <f11:property name="maximums">
                        <f11:element value="50.0"/>
                        <f11:element value="100.0"/>
                        <f11:element value="100.0"/>
                    </f11:property>
                    <f11:property name="inputminimums">
                        <f11:element value="0.0"/>
                        <f11:element value="0.0"/>
                        <f11:element value="0.0"/>
                    </f11:property>
                    <f11:property name="inputmaximums">
                        <f11:element value="4000.0"/>
                        <f11:element value="4000.0"/>
                        <f11:element value="4000.0"/>
                    </f11:property>
                    <f11:holder name="holder">
                        <f11:name value="P1_D_500_BcdSingle"/>
                        <f11:name value="P1_D_501_BcdSingle"/>
                        <f11:name value="P1_D_503_BcdSingle"/>
                    </f11:holder>
                    <f11:property name="pointname">
                        <f11:name value="$(36_unit) $(36_name)"/>
                        <f11:name value="$(37_unit) $(37_name)"/>
                        <f11:name value="$(39_unit) $(39_name)"/>
                    </f11:property>
                    <f11:property name="unitmark">
                        <f11:name value="$(36_unit_mark)"/>
                        <f11:name value="$(37_unit_mark)"/>
                        <f11:name value="$(39_unit_mark)"/>
                    </f11:property>
                </f11:series>
            </f11:graphproperty>
        </f11:trendgraph2>
    </f11:page>
