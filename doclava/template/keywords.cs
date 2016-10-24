<?cs include:"macros.cs" ?>
<html>
<?cs include:"head_tag.cs" ?>
<?cs include:"header.cs" ?>

<div class="g-unit" id="doc-content">

<div id="jd-header">
<h1><?cs var:page.title ?></h1>
</div>

<div id="jd-content">

<div class="jd-letterlist"><?cs each:letter=keywords ?>
    <a href="#letter_<?cs name:letter ?>"><?cs name:letter ?></a><?cs /each?>
</div>

<?cs each:letter=keywords ?>
<a id="letter_<?cs name:letter ?>"></a>
<h2><?cs name:letter ?></h2>
<ul class="jd-letterentries">
<?cs each:entry=letter
?>  <li><a href="<?cs var:toroot ?><?cs var:entry.href ?>"><?cs var:entry.label
        ?></a>&nbsp;<font class="jd-letterentrycomments">(<?cs var:entry.comment ?>)</font></li>
<?cs /each
?></ul>

<?cs /each ?>

<?cs include:"footer.cs" ?>
</div><!-- end jd-content -->
</div><!-- end doc-content -->

<?cs include:"trailer.cs" ?>

</body>
</html>
