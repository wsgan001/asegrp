<map version="0.8.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1255136433031" ID="Freemind_Link_705748074" MODIFIED="1255138627593" TEXT="Residual Coverage Analysis of DSE">
<node CREATED="1255140558718" ID="Freemind_Link_1696610849" MODIFIED="1255140565234" POSITION="right" TEXT="Objective">
<node CREATED="1255140573906" ID="Freemind_Link_464495494" MODIFIED="1255140629734" TEXT="reports non-covered branches with related issues"/>
<node CREATED="1255140601921" ID="Freemind_Link_1461125760" MODIFIED="1255140611484" TEXT="filter out unrelevant issues"/>
<node CREATED="1255140613921" ID="Freemind_Link_410453861" MODIFIED="1255140623718" TEXT="provide user suggestions"/>
</node>
<node CREATED="1255136486921" ID="_" MODIFIED="1255139776531" POSITION="right" TEXT="Approach">
<node CREATED="1255139958781" ID="Freemind_Link_1709266891" MODIFIED="1255140426203" TEXT="define categories of issues">
<node CREATED="1255140489281" ID="Freemind_Link_1413223171" MODIFIED="1255140639906" TEXT="object creation issue"/>
<node CREATED="1255140493296" ID="Freemind_Link_1972177056" MODIFIED="1255140659750" TEXT="external library deoendency, like uninstrumented method invocation"/>
<node CREATED="1255140494421" ID="Freemind_Link_1824795439" MODIFIED="1255140670250" TEXT="environment dependency"/>
<node CREATED="1255140495234" ID="Freemind_Link_1780461933" MODIFIED="1255140693375" TEXT="exceed path bounds"/>
</node>
<node CREATED="1255140318578" ID="Freemind_Link_11971907" MODIFIED="1255140349234" TEXT="Collect coverage data, reported issues and filed access information">
<node CREATED="1255140701812" ID="Freemind_Link_680543839" MODIFIED="1255140712593" TEXT="non-covered branches"/>
<node CREATED="1255140714937" ID="Freemind_Link_564625710" MODIFIED="1255140827984" TEXT="field access information"/>
<node CREATED="1255140828984" ID="Freemind_Link_493856756" MODIFIED="1255140846234" TEXT="track return value of uninstrumented method invocation"/>
<node CREATED="1255140847343" ID="Freemind_Link_1333362900" MODIFIED="1255141386671" TEXT="check whether non-covered branch is associated with static method call or system library call(ADO.NET, Socket, File and so on)"/>
<node CREATED="1255140848703" ID="Freemind_Link_1494797746" MODIFIED="1255141419390" TEXT="find pattern in the problem log and get the path boundary information"/>
</node>
<node CREATED="1255140334062" ID="Freemind_Link_878003983" MODIFIED="1255140442843" TEXT="filter out useless data">
<node CREATED="1255140864625" ID="Freemind_Link_925824412" MODIFIED="1255140884796" TEXT="filter out non-covered branches whose target statements are covered"/>
<node CREATED="1255140886640" ID="Freemind_Link_1103637563" MODIFIED="1255141438281" TEXT="filter out filed access information of covered branches"/>
</node>
<node CREATED="1255140446265" ID="Freemind_Link_838506487" MODIFIED="1255140464265" TEXT="filter out unrelevant issues">
<node CREATED="1255141495937" ID="Freemind_Link_1636786559" MODIFIED="1255141538265" TEXT="uninstrumented issue not related to non-covered branch"/>
<node CREATED="1255141552734" ID="Freemind_Link_1239743841" MODIFIED="1255141564218" TEXT="object creation issue not related to non-covered branch"/>
</node>
<node CREATED="1255140471625" ID="Freemind_Link_195907672" MODIFIED="1255140484609" TEXT="classfy issues and assign them to related branches"/>
</node>
<node CREATED="1255136509000" ID="Freemind_Link_218680303" MODIFIED="1255139700250" POSITION="left" TEXT="Motivation">
<node CREATED="1255136677390" ID="Freemind_Link_1042142143" MODIFIED="1255138575968" TEXT="Most DSE tools could not achieve full coverage of statements"/>
<node CREATED="1255136697468" HGAP="33" ID="Freemind_Link_541503683" MODIFIED="1255139875015" TEXT="There are different kinds of issues " VSHIFT="-9"/>
<node CREATED="1255136684890" ID="Freemind_Link_689419792" MODIFIED="1255138600656" TEXT="Reporting every issue encountered is not so useful for user"/>
<node CREATED="1255139822000" ID="Freemind_Link_364686667" MODIFIED="1255139825234" TEXT="Reading the coverage report and a long list of issues can be time consuming "/>
</node>
<node CREATED="1255136538734" ID="Freemind_Link_1554545024" MODIFIED="1255136553984" POSITION="right" TEXT="Experiments" VSHIFT="19">
<node CREATED="1255139990406" ID="Freemind_Link_1938400271" MODIFIED="1255140004359" TEXT="NHibernate"/>
<node CREATED="1255140008593" ID="Freemind_Link_902895685" MODIFIED="1255140018812" TEXT="Xunit"/>
<node CREATED="1255141330656" ID="Freemind_Link_678827984" MODIFIED="1255141332515" TEXT="Snmp"/>
</node>
</node>
</map>
