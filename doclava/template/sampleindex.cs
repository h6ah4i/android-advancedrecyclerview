<?cs include:"doctype.cs" ?>
<?cs include:"macros.cs" ?>
<?cs set:resources="true" ?>
<html>
<?cs include:"head_tag.cs" ?>
<?cs include:"header.cs" ?>
<body class="gc-documentation">


<a id="top"></a>
<div class="g-unit" id="doc-content">
 <div id="jd-header" class="guide-header">
  <span class="crumb">&nbsp;</span>
  <h1><?cs var:page.title ?></h1>
 </div>

<div id="jd-content">
<p><a href="../index.html">&larr; Back</a></p>

<?cs var:summary ?>

  <?cs if:subcount(subdirs) ?>
      <h2>Subdirectories</h2>
      <ul class="nolist">
      <?cs each:dir=subdirs ?>
        <li><a href="<?cs var:dir.name ?>/index.html"><?cs
          var:dir.name ?>/</a></li>
      <?cs /each ?>
      </ul>
  <?cs /if ?>

  <?cs if:subcount(files) ?>
      <h2>Files</h2>
      <ul class="nolist">
      <?cs each:file=files ?>
        <li><a href="<?cs var:file.href ?>"><?cs
          var:file.name ?></a></li>
      <?cs /each ?>
      </ul>
  <?cs /if ?>

</div><!-- end jd-content -->

<?cs include:"footer.cs" ?>

</div><!-- end doc-content -->

<?cs include:"trailer.cs" ?>

</body>
</html>
