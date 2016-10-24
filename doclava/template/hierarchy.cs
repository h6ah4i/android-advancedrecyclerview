<?cs include:"macros.cs" ?>
<html>
<style>
    .jd-hierarchy-spacer {
        width: 15px;
    }
    .jd-hierarchy-data {
        text-align: left;
        vertical-align: top;
    }
</style>
<?cs include:"head_tag.cs" ?>
<?cs include:"header.cs" ?>

<div class="g-unit" id="doc-content">

<div id="jd-header">
<h1><?cs var:page.title ?></h1>
</div>

<div id="jd-content">

<div style="margin-left: 20px; margin-right: 20px;">

<?cs def:hierarchy_list(classes) ?>
<?cs each:cl = classes ?>
<tr>
    <?cs loop:x=#0,cl.indent,#1 ?><td class="jd-hierarchy-spacer"></td><?cs /loop ?>
    <td class="jd-hierarchy-data" colspan="<?cs var:cl.colspan ?>">
    <?cs if:cl.exists ?>
        <?cs call:type_link(cl.class) ?>
    <?cs else ?>
        <?cs var:cl.value ?>
    <?cs /if ?>
    </td>
    <td class="jd-hierarchy-data">
    <?cs each:iface = cl.interfaces ?>
        <?cs if:iface.exists ?>
            <?cs call:type_link(iface.class) ?>
        <?cs else ?>
            <?cs var:iface.value ?>
        <?cs /if ?> &nbsp;&nbsp;
    <?cs /each ?>
    &nbsp;
    </td>
</tr>
<?cs call:hierarchy_list(cl.derived) ?>
<?cs /each ?>
<?cs /def ?>


<table border="0" cellpadding="0" cellspacing="1">
<th class="jd-hierarchy-data" colspan="<?cs var:colspan ?>">Class</th>
<th class="jd-hierarchy-data">Interfaces</th>
<?cs call:hierarchy_list(classes) ?>
</table>

</div>

<?cs include:"footer.cs" ?>
</div><!-- end jd-content -->
</div><!-- end doc-content -->

<?cs include:"trailer.cs" ?>

</body>
</html>

