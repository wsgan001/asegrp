<map version="0.8.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1255136433031" ID="Freemind_Link_705748074" MODIFIED="1255138627593" TEXT="Residual Coverage Analysis of DSE">
<node CREATED="1255142683859" ID="Freemind_Link_1300703508" MODIFIED="1255142685953" POSITION="right" TEXT="Example">
<node CREATED="1255147455656" ID="Freemind_Link_1724968391" MODIFIED="1255147465796" TEXT="object creation example">
<node CREATED="1255273728562" ID="Freemind_Link_871644295" MODIFIED="1255273733421" TEXT="Show an example"/>
<node CREATED="1255273749640" ID="Freemind_Link_757021719" MODIFIED="1255273767171" TEXT="Explain why current DSE tools could not generate input"/>
<node CREATED="1255273769203" ID="Freemind_Link_318117201" MODIFIED="1255273793203" TEXT="Briefly introduce our approach to deal with it"/>
</node>
<node CREATED="1255147466609" ID="Freemind_Link_182417129" MODIFIED="1255147484375" TEXT="uninstrumented example">
<node CREATED="1255273796593" ID="Freemind_Link_24648991" MODIFIED="1255273838687" TEXT="Show an example with one external library call involved in constraint and other not"/>
<node CREATED="1255273853578" ID="Freemind_Link_1022155066" MODIFIED="1255273879031" TEXT="Explain why DSE tool does not monitor all library by default"/>
<node CREATED="1255273883515" ID="Freemind_Link_686574469" MODIFIED="1255273910812" TEXT="Briefly discuss our approach"/>
</node>
<node CREATED="1255147467750" ID="Freemind_Link_1150465565" MODIFIED="1255147499562" TEXT="environment dependency example, snmp"/>
<node CREATED="1255147468968" ID="Freemind_Link_1224947202" MODIFIED="1255147509812" TEXT="loop example"/>
</node>
<node CREATED="1255136486921" ID="_" MODIFIED="1255139776531" POSITION="right" TEXT="Approach">
<node CREATED="1255273984031" ID="Freemind_Link_520663475" MODIFIED="1255273990015" TEXT="Overview">
<node CREATED="1255273998296" ID="Freemind_Link_272313555" MODIFIED="1255274019625" TEXT="high level design of Covana, three main components"/>
<node CREATED="1255274020843" ID="Freemind_Link_1127960993" MODIFIED="1255274032875" TEXT="introduce each component"/>
</node>
<node CREATED="1255139948187" ID="Freemind_Link_1929316893" MODIFIED="1255139965781" TEXT="Observer Component">
<node CREATED="1255140028640" ID="Freemind_Link_645476171" MODIFIED="1255140056625" TEXT="Coverage Observer">
<node CREATED="1255274050671" ID="Freemind_Link_1001265615" MODIFIED="1255274058218" TEXT="collect branch coverage"/>
<node CREATED="1255274058796" ID="Freemind_Link_1813953585" MODIFIED="1255274063078" TEXT="IL information"/>
</node>
<node CREATED="1255140031531" ID="Freemind_Link_1715216914" MODIFIED="1255140073125" TEXT="Field Access Observer">
<node CREATED="1255274149625" ID="Freemind_Link_1439084159" MODIFIED="1255274176437" TEXT="collect field access information when pex fail to negate a constraint"/>
</node>
<node CREATED="1255140032796" ID="Freemind_Link_112764429" MODIFIED="1255140077828" TEXT="Issue Observer">
<node CREATED="1255274133343" ID="Freemind_Link_775115319" MODIFIED="1255274141250" TEXT="collect different kinds of issues"/>
</node>
</node>
<node CREATED="1255139956562" ID="Freemind_Link_1293598322" MODIFIED="1255139975531" TEXT="Pre-processing Component">
<node CREATED="1255140089609" ID="Freemind_Link_1679315382" MODIFIED="1255140113390" TEXT="filter out useless non-covered branch">
<node CREATED="1255274250765" ID="Freemind_Link_1950972447" MODIFIED="1255274258890" TEXT="show example"/>
<node CREATED="1255274260156" ID="Freemind_Link_659990781" MODIFIED="1255274268328" TEXT="discuss how to detect the situation"/>
<node CREATED="1255274269437" ID="Freemind_Link_1006442835" MODIFIED="1255274279109" TEXT="may consider multi-clause"/>
</node>
<node CREATED="1255140116281" ID="Freemind_Link_5407229" MODIFIED="1255140131625" TEXT="filter out field access information for covered branches">
<node CREATED="1255274317140" ID="Freemind_Link_1149973675" MODIFIED="1255274331687" TEXT="explain why we need to do the filtering"/>
</node>
</node>
<node CREATED="1255139958781" ID="Freemind_Link_1709266891" MODIFIED="1255139983515" TEXT="Analysis Component">
<node CREATED="1255145197687" ID="Freemind_Link_83346426" MODIFIED="1255145261703" TEXT="find out object creation issue by looking at the type of object creation issues reported and the type of field access information"/>
<node CREATED="1255145352171" ID="Freemind_Link_1078205253" MODIFIED="1255147414000" TEXT="find out uninstrumented issue by looking at whether it is involved in the non-covered branches"/>
</node>
</node>
<node CREATED="1255136509000" ID="Freemind_Link_218680303" MODIFIED="1255142649906" POSITION="left" TEXT="Abstract">
<node CREATED="1255136677390" ID="Freemind_Link_1042142143" MODIFIED="1255138575968" TEXT="Most DSE tools could not achieve full coverage of statements"/>
<node CREATED="1255136684890" ID="Freemind_Link_689419792" MODIFIED="1255138600656" TEXT="Reporting every issue encountered is not so useful for user"/>
<node CREATED="1255139822000" ID="Freemind_Link_364686667" MODIFIED="1255139825234" TEXT="Reading the coverage report and a long list of issues can be time consuming "/>
<node CREATED="1255144365453" ID="Freemind_Link_1705185032" MODIFIED="1255144389093" TEXT="We provide Covana to address the problem"/>
<node CREATED="1255144390296" ID="Freemind_Link_1431651637" MODIFIED="1255144397234" TEXT="Evaluation Result"/>
</node>
<node CREATED="1255136538734" ID="Freemind_Link_1554545024" MODIFIED="1255136553984" POSITION="right" TEXT="Experiments" VSHIFT="19">
<node CREATED="1255139990406" ID="Freemind_Link_1938400271" MODIFIED="1255140004359" TEXT="NHibernate"/>
<node CREATED="1255140008593" ID="Freemind_Link_902895685" MODIFIED="1255140018812" TEXT="Xunit"/>
<node CREATED="1255142695500" ID="Freemind_Link_240450418" MODIFIED="1255142701015" TEXT="Snmp"/>
</node>
<node CREATED="1255142652781" ID="Freemind_Link_1239332378" MODIFIED="1255142656609" POSITION="left" TEXT="Introduction">
<node CREATED="1255144408156" ID="Freemind_Link_1308883666" MODIFIED="1255144469812" TEXT="High coverage indicates testing thoroughness and confidence"/>
<node CREATED="1255144471718" ID="Freemind_Link_1952790685" MODIFIED="1255144494812" TEXT="DSE tool is aiming at achieving high coverage but has its limitation"/>
<node CREATED="1255144508937" ID="Freemind_Link_1796335190" MODIFIED="1255144553031" TEXT="When fail to find out new paths, current tool will report every issue, which is not so useful"/>
<node CREATED="1255144675375" ID="Freemind_Link_1575967643" MODIFIED="1255144847000" TEXT="We propose Covana to address this problem">
<node CREATED="1255144888562" ID="Freemind_Link_1183892626" MODIFIED="1255144891703" TEXT="Define different categories of issues"/>
<node CREATED="1255144892640" ID="Freemind_Link_1853573419" MODIFIED="1255145156531" TEXT="Provides observers to obtain data"/>
<node CREATED="1255144894203" ID="Freemind_Link_523439591" MODIFIED="1255145163796" TEXT="Pre-process data"/>
<node CREATED="1255145166765" ID="Freemind_Link_1228383905" MODIFIED="1255145175812" TEXT="Analyse the data and report"/>
</node>
</node>
</node>
</map>
