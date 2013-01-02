<?xml version='1.0' encoding='UTF-8' ?> 
<!-- was: <?xml version="1.0" encoding="ISO-8859-1"?> -->

<xsl:stylesheet version="1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" version="1.0" indent="yes" encoding="UTF-8"/>
  
    <xsl:template match="caches">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    
            <fo:layout-master-set>
                <fo:simple-page-master  master-name="summarypage"
                                        page-height="11in"
                                        page-width="8.5in"
                                        margin-top="1in"
                                        margin-bottom="1in"
                                        margin-left="1in"
                                        margin-right="1in"
                >
                    <fo:region-body />
                </fo:simple-page-master>
            </fo:layout-master-set>
    
            <fo:page-sequence master-reference="summarypage">
                <fo:flow flow-name="xsl-region-body">
        
                    <!-- Header -->
                    <fo:block text-align="center" font-size="18pt" font-weight="bold">
                        Geocaches
                    </fo:block>
                    
                    <!-- Caches -->
                    <fo:block text-align="center" font-size="8pt" space-before="3em">
                        <xsl:for-each select="cache">
                            
                            <fo:table>
                                <fo:table-column column-width="80%"/>
                                <fo:table-column />

                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell>
                                            <fo:block text-align="left" padding-top="0.5em" font-weight="bold">
                                                <xsl:value-of select="label"/>
                                                <xsl:value-of select="name"/> (<xsl:value-of select="GC"/>)
                                            </fo:block>
                                            <fo:block text-align="left" padding-top="0.5em" font-style="italic">
                                                <xsl:value-of select="coords"/>
                                            </fo:block>
                                            <fo:block text-align="left" padding-top="0.5em" font-weight="bold" color="red">
                                                <xsl:value-of select="warning"/>
                                            </fo:block>
                                            <fo:block text-align="left" padding-top="0.5em">
                                                <xsl:value-of select="description"/>
                                            </fo:block>
                                            <xsl:if test="/caches/cache[position()]/hint">
                                                <fo:block text-align="left" padding-top="0.5em" font-style="italic">
                                                    Hint: 
                                                    <xsl:value-of select="hint"/>
                                                </fo:block>
                                            </xsl:if>
                                        </fo:table-cell>
                                        <fo:table-cell display-align="center">
                                            <fo:block text-align="right">
                                                <fo:external-graphic content-width="1in" content-height="1in">
                                                    <xsl:attribute name="src">
                                                        <xsl:value-of select="qr"/>
                                                    </xsl:attribute>
                                                </fo:external-graphic>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>
                            
                        </xsl:for-each>
                    </fo:block>
                    
                    <!--                    <fo:block text-align="center" font-size="18pt" font-weight="bold" page-break-before="always">
                        Map Overview
                    </fo:block>-->
                    <!--                    <fo:block text-align="center" font-size="8pt">
                        The numbers on the map correspond to the caches on the previous page, in order.
                    </fo:block>-->
                    
                    <fo:block text-align="center">
                        <xsl:for-each select="map"> 
                            <fo:external-graphic height="10in" width="7in" content-height="scale-down-to-fit" content-width="scale-down-to-fit">
                                <xsl:attribute name="src">
                                    <xsl:value-of select="image"/>
                                </xsl:attribute>
                            </fo:external-graphic>
                        </xsl:for-each>
                    </fo:block>
        
                </fo:flow>
            </fo:page-sequence>
        </fo:root> 
    </xsl:template>
</xsl:stylesheet>