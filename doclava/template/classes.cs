<?cs include:"doctype.cs" ?>
<?cs include:"macros.cs" ?>
<html>
<?cs include:"head_tag.cs" ?>
<?cs include:"header.cs" ?>

<div class="g-unit" id="doc-content">

<div id="jd-header">
<h1><?cs var:page.title ?></h1>
</div>

<div id="jd-content">

<div class="jd-letterlist"><?cs each:letter=docs.classes ?>
    <a href="#letter_<?cs name:letter ?>"><?cs name:letter ?></a><?cs /each?>
</div>

<?cs each:letter=docs.classes ?>
<?cs set:count = #1 ?>
<h2 id="letter_<?cs name:letter ?>"><?cs name:letter ?></h2>
<table class="jd-sumtable">
    <?cs set:cur_row = #0 ?>
    <?cs each:cl = letter ?>
        <tr class="<?cs if:count % #2 ?>alt-color<?cs /if ?> api apilevel-<?cs var:cl.since.key ?>" >
            <td class="jd-linkcol"><?cs call:type_link(cl.type) ?></td>
            <td class="jd-descrcol" width="100%"><?cs call:short_descr(cl) ?>&nbsp;</td>
        </tr>
    <?cs set:count = count + #1 ?>
    <?cs /each ?>
</table>
<?cs /each ?>

<?cs include:"footer.cs" ?>
</div><!-- end jd-content -->
</div><!-- end doc-content -->

<?cs include:"trailer.cs" ?>

</body>
</html>