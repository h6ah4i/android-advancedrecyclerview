
<style>

.package-label {

}

.class-label {
  padding-left: 40px;
}

.method-label {
  padding-left: 80px;
}

.package-entry {
  background-color: #778899;
}

.class-entry {
  background-color: #a9a9a9;
}

.method-entry {
  background-color: #dcdcdc;
}

.collapsed {
  
}

.handle {
  width: 25px;
  background-repeat: no-repeat;
}

.handle-opened {
  background-image: url("<?cs var:triangle.opened ?>");
}

.handle-closed {
  background-image: url("<?cs var:triangle.closed?>");
}

.tbody {
  padding: 0;
  margin: 0;
}

#hierarchy {
  border-collapse:collapse;
}

</style>

<script type="text/javascript"
    src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
    
<script type="text/javascript">
function collapsePackage(tr) {
  var table = $("#hierarchy")[0];
  
  if (!$(tr).hasClass("collapsable")) {
    return;
  }
  
  if ($(tr).hasClass("collapsed")) {
    $(tr).removeClass("collapsed");
    $(tr).children(".handle").addClass("handle-opened");
    $(tr).children(".handle").removeClass("handle-closed");
    if (tr.rowIndex + 1 < table.rows.length) {
      var tbody = table.rows[tr.rowIndex + 1].parentNode;
      $(tbody).show();
    }
  } else {
    $(tr).addClass("collapsed");
    $(tr).children(".handle").removeClass("handle-opened");
    $(tr).children(".handle").addClass("handle-closed");
    if (tr.rowIndex + 1 < table.rows.length
        && $(table.rows[tr.rowIndex + 1]).hasClass("class-entry")) {
      var tbody = table.rows[tr.rowIndex + 1].parentNode;
      $(tbody).hide();
    }
  }
}

function collapseClass(tr) {
  var table = $("#hierarchy")[0];
  
  if (!$(tr).hasClass("collapsable")) {
    return;
  }
  
  if ($(tr).hasClass("collapsed")) {
    $(tr).removeClass("collapsed");
    $(tr).children(".handle").addClass("handle-opened");
    $(tr).children(".handle").removeClass("handle-closed");
    var i = tr.rowIndex + 1;
    while (i < table.rows.length && $(table.rows[i]).hasClass("method-entry")) {
      $(table.rows[i++]).show();
    }
  } else {
    $(tr).addClass("collapsed");
    $(tr).children(".handle").removeClass("handle-opened");
    $(tr).children(".handle").addClass("handle-closed");
    var i = tr.rowIndex + 1;
    while (i < table.rows.length && $(table.rows[i]).hasClass("method-entry")) {
      $(table.rows[i++]).hide();
    }
  }
}
  
$(function() {  
  $(".package-entry").click(function() {
    collapsePackage(this);
  });
  
  $(".class-entry").click(function() {
    collapseClass(this);
  });
});
</script>

<table border="0" id="hierarchy">
<tr>
<th> </th>
<?cs each:site = sites ?>
<th><?cs var:site.name ?></th>
<?cs /each ?>
</tr>
<?cs each:package = packages ?>
  <?cs if:subcount(package.classes) ?>
    <tr class="package-entry collapsable">
    <td class="handle handle-opened">&nbsp;</td>
  <?cs else ?>
    <tr class="package-entry">
    <td class="handle">&nbsp;</td>
  <?cs /if ?>
  <td class="package-label"><?cs var:package.name ?></td>
  <?cs each:site = package.sites ?>
    <td>
    <?cs if:site.hasPackage ?>
    <a href="<?cs var:site.link ?>">Link</a>
    <?cs else ?>
    N/A
    <?cs /if ?>
    </td>
  <?cs /each ?>
  </tr>
  <tbody class="package-contents">
  <?cs each:class = package.classes ?>
    <?cs if:subcount(class.methods) ?>
      <tr class="class-entry collapsable">
      <td class="handle handle-opened">&nbsp;</td>
    <?cs else ?>
      <tr class="class-entry">
      <td class="handle">&nbsp;</td>
    <?cs /if ?>
      <td class="class-label"><?cs var:class.qualifiedName ?></td>
      <?cs each:site = class.sites ?>
        <td>
        <?cs if:site.hasClass ?>
        <a href="<?cs var:site.link ?>">Link</a>
        <?cs else ?>
        N/A
        <?cs /if ?>
        </td>
      <?cs /each ?>
    </tr>
    <?cs each:method = class.methods ?>
    <tr class="method-entry">
      <td class="handle">&nbsp;</td>
      <td class="method-label"><?cs var:method.signature ?></td>
      <?cs each:site = method.sites ?>
        <td>
        <?cs if:site.hasMethod ?>
        <a href="<?cs var:site.link ?>">Link</a>
        <?cs else ?>
        N/A
        <?cs /if ?>
        </td>
      <?cs /each ?>
    </tr>
    <?cs /each ?><?cs # methods ?>
  <?cs /each ?><?cs # classes ?>
  </tbody>
<?cs /each ?><?cs # packages ?>
</table>