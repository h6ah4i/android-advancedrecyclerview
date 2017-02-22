<?cs include:"doctype.cs" ?>
<?cs include:"macros.cs" ?>
<html>
<?cs include:"head_tag.cs" ?>
<body class="<?cs var:class.since.key ?>">
<?cs include:"header.cs" ?>

<div class="g-unit" id="doc-content">

<div id="api-info-block">

<?cs # are there inherited members ?>
<?cs each:cl=class.inherited ?>
  <?cs if:subcount(cl.methods) ?>
   <?cs set:inhmethods = #1 ?>
  <?cs /if ?>
  <?cs if:subcount(cl.constants) ?>
   <?cs set:inhconstants = #1 ?>
  <?cs /if ?>
  <?cs if:subcount(cl.fields) ?>
   <?cs set:inhfields = #1 ?>
  <?cs /if ?>
  <?cs if:subcount(cl.attrs) ?>
   <?cs set:inhattrs = #1 ?>
  <?cs /if ?>
<?cs /each ?>

<div class="sum-details-links">
<?cs if:doclava.generate.sources ?>
<div>
<a href="<?cs var:class.name ?>-source.html">View Source</a>
</div>
<?cs /if ?>
<?cs if:inhattrs || inhconstants || inhfields || inhmethods || (!class.subclasses.hidden &&
     (subcount(class.subclasses.direct) || subcount(class.subclasses.indirect))) ?>
Summary:
<?cs if:subcount(class.inners) ?>
  <a href="#nestedclasses">Nested Classes</a>
  <?cs set:linkcount = #1 ?>
<?cs /if ?>
<?cs if:subcount(class.attrs) ?>
  <?cs if:linkcount ?>&#124; <?cs /if ?><a href="#lattrs">XML Attrs</a>
  <?cs set:linkcount = #1 ?>
<?cs /if ?>
<?cs if:inhattrs ?>
  <?cs if:linkcount ?>&#124; <?cs /if ?><a href="#inhattrs">Inherited XML Attrs</a>
  <?cs set:linkcount = #1 ?>
<?cs /if ?>
<?cs if:subcount(class.enumConstants) ?>
  <?cs if:linkcount ?>&#124; <?cs /if ?><a href="#enumconstants">Enums</a>
  <?cs set:linkcount = #1 ?>
<?cs /if ?>
<?cs if:subcount(class.constants) ?>
  <?cs if:linkcount ?>&#124; <?cs /if ?><a href="#constants">Constants</a>
  <?cs set:linkcount = #1 ?>
<?cs /if ?>
<?cs if:inhconstants ?>
  <?cs if:linkcount ?>&#124; <?cs /if ?><a href="#inhconstants">Inherited Constants</a>
  <?cs set:linkcount = #1 ?>
<?cs /if ?>
<?cs if:subcount(class.fields) ?>
  <?cs if:linkcount ?>&#124; <?cs /if ?><a href="#lfields">Fields</a>
  <?cs set:linkcount = #1 ?>
<?cs /if ?>
<?cs if:inhfields ?>
  <?cs if:linkcount ?>&#124; <?cs /if ?><a href="#inhfields">Inherited Fields</a>
  <?cs set:linkcount = #1 ?>
<?cs /if ?>
<?cs if:subcount(class.ctors.public) ?>
  <?cs if:linkcount ?>&#124; <?cs /if ?><a href="#pubctors">Ctors</a>
  <?cs set:linkcount = #1 ?>
<?cs /if ?>
<?cs if:subcount(class.ctors.protected) ?>
  <?cs if:linkcount ?>&#124; <?cs /if ?><a href="#proctors">Protected Ctors</a>
  <?cs set:linkcount = #1 ?>
<?cs /if ?>
<?cs if:subcount(class.methods.public) ?>
  <?cs if:linkcount ?>&#124; <?cs /if ?><a href="#pubmethods">Methods</a>
  <?cs set:linkcount = #1 ?>
<?cs /if ?>
<?cs if:subcount(class.methods.protected) ?>
  <?cs if:linkcount ?>&#124; <?cs /if ?><a href="#promethods">Protected Methods</a>
  <?cs set:linkcount = #1 ?>
<?cs /if ?>
<?cs if:inhmethods ?>
  <?cs if:linkcount ?>&#124; <?cs /if ?><a href="#inhmethods">Inherited Methods</a>
<?cs /if ?>
&#124; <a href="#" onclick="return toggleAllClassInherited()" id="toggleAllClassInherited">[Expand All]</a>
<?cs /if ?>
</div><!-- end sum-details-links -->
<div class="api-level">
  <?cs call:since_tags(class) ?>
  <?cs call:federated_refs(class) ?>
</div>
</div><!-- end api-info-block -->

<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- ======== START OF CLASS DATA ======== -->

<div id="jd-header">
    <?cs var:class.scope ?>
    <?cs var:class.static ?> 
    <?cs var:class.final ?> 
    <?cs var:class.abstract ?>
    <?cs var:class.kind ?>
<h1><?cs var:class.name ?></h1>

<?cs set:colspan = subcount(class.inheritance) ?>
<?cs each:supr = class.inheritance ?>
  <?cs if:colspan == 2 ?>
    extends <?cs call:type_link(supr.short_class) ?><br/>
  <?cs /if ?>
  <?cs if:last(supr) && subcount(supr.interfaces) ?>
      implements 
      <?cs each:t=supr.interfaces ?>
        <?cs call:type_link(t) ?> 
      <?cs /each ?>
  <?cs /if ?>
  <?cs set:colspan = colspan-1 ?>
<?cs /each ?>

</div><!-- end header -->

<div id="naMessage"></div>

<div id="jd-content" class="api apilevel-<?cs var:class.since.key ?>">
<table class="jd-inheritance-table">
<?cs set:colspan = subcount(class.inheritance) ?>
<?cs each:supr = class.inheritance ?>
    <tr>
        <?cs loop:i = 1, (subcount(class.inheritance)-colspan), 1 ?>
            <td class="jd-inheritance-space">&nbsp;<?cs if:(subcount(class.inheritance)-colspan) == i ?>&nbsp;&nbsp;&#x21b3;<?cs /if ?></td>
        <?cs /loop ?> 	
        <td colspan="<?cs var:colspan ?>" class="jd-inheritance-class-cell"><?cs
            if:colspan == 1
                ?><?cs call:class_name(class.qualifiedType) ?><?cs 
            else 
                ?><?cs call:type_link(supr.class) ?><?cs
            /if ?></td>
    </tr>
    <?cs set:colspan = colspan-1 ?>
<?cs /each ?>
</table>

<?cs # this next line must be exactly like this to be parsed by eclipse ?>

<?cs if:subcount(class.subclasses.direct) && !class.subclasses.hidden ?>
<table class="jd-sumtable jd-sumtable-subclasses"><tr><td colspan="12" style="border:none;margin:0;padding:0;">
<?cs call:expando_trigger("subclasses-direct", "closed") ?>Known Direct Subclasses
<?cs call:expandable_class_list("subclasses-direct", class.subclasses.direct, "list") ?>
</td></tr></table>
<?cs /if ?>

<?cs if:subcount(class.subclasses.indirect) && !class.subclasses.hidden ?>
<table class="jd-sumtable jd-sumtable-subclasses"><tr><td colspan="12" style="border:none;margin:0;padding:0;">
<?cs call:expando_trigger("subclasses-indirect", "closed") ?>Known Indirect Subclasses
<?cs call:expandable_class_list("subclasses-indirect", class.subclasses.indirect, "list") ?>
</td></tr></table>
<?cs /if ?>

<div class="jd-descr">
<?cs call:deprecated_warning(class) ?>
<?cs if:subcount(class.descr) ?>
<h2>Class Overview</h2>
<p><?cs call:tag_list(class.descr) ?></p>
<?cs /if ?>

<?cs call:see_also_tags(class.seeAlso) ?>

</div><!-- jd-descr -->


<?cs # summary macros ?>

<?cs def:write_method_summary(methods, included) ?>
<?cs set:count = #1 ?>
<?cs each:method = methods ?>
	 <?cs # The apilevel-N class MUST BE LAST in the sequence of class names ?>
    <tr class="<?cs if:count % #2 ?>alt-color<?cs /if ?> api apilevel-<?cs var:method.since.key ?>" >
        <td class="jd-typecol">
            <?cs var:method.abstract ?>
            <?cs var:method.synchronized ?>
            <?cs var:method.final ?>
            <?cs var:method.static ?>
            <?cs call:type_link(method.generic) ?>
            <?cs call:type_link(method.returnType) ?>
        </td>
        <td class="jd-linkcol" width="100%">
        <span class="sympad"><?cs call:cond_link(method.name, toroot, method.href, included) ?></span>(<?cs call:parameter_list(method.params) ?>)
        <?cs if:subcount(method.shortDescr) || subcount(method.deprecated) ?>
        <div class="jd-descrdiv"><?cs call:short_descr(method) ?></div>
  <?cs /if ?>
  </td></tr>
<?cs set:count = count + #1 ?>
<?cs /each ?>
<?cs /def ?>

<?cs def:write_field_summary(fields, included) ?>
<?cs set:count = #1 ?>
    <?cs each:field=fields ?>
      <tr class="<?cs if:count % #2 ?>alt-color<?cs /if ?> api apilevel-<?cs var:field.since.key ?>" >
          <td class="jd-typecol">
          <?cs var:field.scope ?>
          <?cs var:field.static ?>
          <?cs var:field.final ?>
          <?cs call:type_link(field.type) ?></td>
          <td class="jd-linkcol"><?cs call:cond_link(field.name, toroot, field.href, included) ?></td>
          <td class="jd-descrcol" width="100%"><?cs call:short_descr(field) ?></td>
      </tr>
      <?cs set:count = count + #1 ?>
    <?cs /each ?>
<?cs /def ?>

<?cs def:write_constant_summary(fields, included) ?>
<?cs set:count = #1 ?>
    <?cs each:field=fields ?>
    <tr class="<?cs if:count % #2 ?>alt-color<?cs /if ?> api apilevel-<?cs var:field.since.key ?>" >
        <td class="jd-typecol"><?cs call:type_link(field.type) ?></td>
        <td class="jd-linkcol"><?cs call:cond_link(field.name, toroot, field.href, included) ?></td>
        <td class="jd-descrcol" width="100%"><?cs call:short_descr(field) ?></td>
    </tr>
    <?cs set:count = count + #1 ?>
    <?cs /each ?>
<?cs /def ?>

<?cs def:write_attr_summary(attrs, included) ?>
<?cs set:count = #1 ?>
    <tr>
        <td><em>Attribute Name</em></td>
        <td><em>Related Method</em></td>
        <td><em>Description</em></td>
    </tr>
    <?cs each:attr=attrs ?>
    <tr class="<?cs if:count % #2 ?>alt-color<?cs /if ?> api apilevel-<?cs var:attr.since.key ?>" >
        <td class="jd-linkcol"><?cs if:included ?><a href="<?cs var:toroot ?><?cs var:attr.href ?>"><?cs /if ?><?cs var:attr.name ?><?cs if:included ?></a><?cs /if ?></td>
        <td class="jd-linkcol"><?cs each:m=attr.methods ?>
            <?cs call:cond_link(m.name, toroot, m.href, included) ?>
            <?cs /each ?>
        </td>
        <td class="jd-descrcol" width="100%"><?cs call:short_descr(attr) ?>&nbsp;</td>
    </tr>
    <?cs set:count = count + #1 ?>
    <?cs /each ?>
<?cs /def ?>

<?cs def:write_inners_summary(classes) ?>
<?cs set:count = #1 ?>
  <?cs each:cl=class.inners ?>
    <tr class="<?cs if:count % #2 ?>alt-color<?cs /if ?> api apilevel-<?cs var:cl.since.key ?>" >
      <td class="jd-typecol">
        <?cs var:cl.scope ?>
        <?cs var:cl.static ?> 
        <?cs var:cl.final ?> 
        <?cs var:cl.abstract ?>
        <?cs var:cl.kind ?></td>
      <td class="jd-linkcol"><?cs call:type_link(cl.type) ?></td>
      <td class="jd-descrcol" width="100%"><?cs call:short_descr(cl) ?>&nbsp;</td>
    </tr>
    <?cs set:count = count + #1 ?>
    <?cs /each ?>
<?cs /def ?>

<?cs # end macros ?>

<div class="jd-descr">
<?cs # make sure there's a summary view to display ?>
<?cs if:subcount(class.inners)
     || subcount(class.attrs)
     || inhattrs
     || subcount(class.enumConstants)
     || subcount(class.constants)
     || inhconstants
     || subcount(class.fields)
     || inhfields
     || subcount(class.ctors.public)
     || subcount(class.ctors.protected)
     || subcount(class.methods.public)
     || subcount(class.methods.protected)
     || inhmethods ?>
<h2>Summary</h2>

<?cs if:subcount(class.inners) ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- ======== NESTED CLASS SUMMARY ======== -->
<table id="nestedclasses" class="jd-sumtable"><tr><th colspan="12">Nested Classes</th></tr>
<?cs call:write_inners_summary(class.inners) ?>
</table>
<?cs /if ?>

<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<?cs if:subcount(class.attrs) ?>
<!-- =========== FIELD SUMMARY =========== -->
<table id="lattrs" class="jd-sumtable"><tr><th colspan="12">XML Attributes</th></tr>
<?cs call:write_attr_summary(class.attrs, 1) ?>
</table>
<?cs /if ?>

<?cs # if there are inherited attrs, write the table ?>
<?cs if:inhattrs ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- =========== FIELD SUMMARY =========== -->
<table id="inhattrs" class="jd-sumtable"><tr><th>
  <a href="#" class="toggle-all" onclick="return toggleAllInherited(this, null)">[Expand]</a>
  <div style="clear:left;">Inherited XML Attributes</div></th></tr>
<?cs each:cl=class.inherited ?>
<?cs if:subcount(cl.attrs) ?>
<tr class="api apilevel-<?cs var:cl.since.key ?>" >
<td colspan="12">
<?cs call:expando_trigger("inherited-attrs-"+cl.qualified, "closed") ?>From <?cs var:cl.kind ?>
<?cs call:cond_link(cl.qualified, toroot, cl.link, cl.included) ?>
<div id="inherited-attrs-<?cs var:cl.qualified ?>">
  <div id="inherited-attrs-<?cs var:cl.qualified ?>-list"
        class="jd-inheritedlinks">
  </div>
  <div id="inherited-attrs-<?cs var:cl.qualified ?>-summary" style="display: none;">
    <table class="jd-sumtable-expando">
    <?cs call:write_attr_summary(cl.attrs, cl.included) ?></table>
  </div>
</div>
</td></tr>
<?cs /if ?>
<?cs /each ?>
</table>
<?cs /if ?>

<?cs if:subcount(class.enumConstants) ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- =========== ENUM CONSTANT SUMMARY =========== -->
<table id="enumconstants" class="jd-sumtable"><tr><th colspan="12">Enum Values</th></tr>
<?cs set:count = #1 ?>
    <?cs each:field=class.enumConstants ?>
    <tr class="<?cs if:count % #2 ?>alt-color<?cs /if ?> api apilevel-<?cs var:field.since.key ?>" >
        <td class="jd-descrcol"><?cs call:type_link(field.type) ?>&nbsp;</td>
        <td class="jd-linkcol"><?cs call:cond_link(field.name, toroot, field.href, cl.included) ?>&nbsp;</td>
        <td class="jd-descrcol" width="100%"><?cs call:short_descr(field) ?>&nbsp;</td>
    </tr>
    <?cs set:count = count + #1 ?>
    <?cs /each ?>
</table>
<?cs /if ?>

<?cs if:subcount(class.constants) ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- =========== ENUM CONSTANT SUMMARY =========== -->
<table id="constants" class="jd-sumtable"><tr><th colspan="12">Constants</th></tr>
<?cs call:write_constant_summary(class.constants, 1) ?>
</table>
<?cs /if ?>

<?cs # if there are inherited constants, write the table ?>
<?cs if:inhconstants ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- =========== ENUM CONSTANT SUMMARY =========== -->
<table id="inhconstants" class="jd-sumtable"><tr><th>
  <a href="#" class="toggle-all" onclick="return toggleAllInherited(this, null)">[Expand]</a>
  <div style="clear:left;">Inherited Constants</div></th></tr>
<?cs each:cl=class.inherited ?>
<?cs if:subcount(cl.constants) ?>
<tr class="api apilevel-<?cs var:cl.since.key ?>" >
<td colspan="12">
<?cs call:expando_trigger("inherited-constants-"+cl.qualified, "closed") ?>From <?cs var:cl.kind ?>
<?cs call:cond_link(cl.qualified, toroot, cl.link, cl.included) ?>
<div id="inherited-constants-<?cs var:cl.qualified ?>">
  <div id="inherited-constants-<?cs var:cl.qualified ?>-list"
        class="jd-inheritedlinks">
  </div>
  <div id="inherited-constants-<?cs var:cl.qualified ?>-summary" style="display: none;">
    <table class="jd-sumtable-expando">
    <?cs call:write_constant_summary(cl.constants, cl.included) ?></table>
  </div>
</div>
</td></tr>
<?cs /if ?>
<?cs /each ?>
</table>
<?cs /if ?>

<?cs if:subcount(class.fields) ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- =========== FIELD SUMMARY =========== -->
<table id="lfields" class="jd-sumtable"><tr><th colspan="12">Fields</th></tr>
<?cs call:write_field_summary(class.fields, 1) ?>
</table>
<?cs /if ?>

<?cs # if there are inherited fields, write the table ?>
<?cs if:inhfields ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- =========== FIELD SUMMARY =========== -->
<table id="inhfields" class="jd-sumtable"><tr><th>
  <a href="#" class="toggle-all" onclick="return toggleAllInherited(this, null)">[Expand]</a>
  <div style="clear:left;">Inherited Fields</div></th></tr>
<?cs each:cl=class.inherited ?>
<?cs if:subcount(cl.fields) ?>
<tr class="api apilevel-<?cs var:cl.since.key ?>" >
<td colspan="12">
<?cs call:expando_trigger("inherited-fields-"+cl.qualified, "closed") ?>From <?cs var:cl.kind ?>
<?cs call:cond_link(cl.qualified, toroot, cl.link, cl.included) ?>
<div id="inherited-fields-<?cs var:cl.qualified ?>">
  <div id="inherited-fields-<?cs var:cl.qualified ?>-list"
        class="jd-inheritedlinks">
  </div>
  <div id="inherited-fields-<?cs var:cl.qualified ?>-summary" style="display: none;">
    <table class="jd-sumtable-expando">
    <?cs call:write_field_summary(cl.fields, cl.included) ?></table>
  </div>
</div>
</td></tr>
<?cs /if ?>
<?cs /each ?>
</table>
<?cs /if ?>

<?cs if:subcount(class.ctors.public) ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- ======== CONSTRUCTOR SUMMARY ======== -->
<table id="pubctors" class="jd-sumtable"><tr><th colspan="12">Public Constructors</th></tr>
<?cs call:write_method_summary(class.ctors.public, 1) ?>
</table>
<?cs /if ?>

<?cs if:subcount(class.ctors.protected) ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- ======== CONSTRUCTOR SUMMARY ======== -->
<table id="proctors" class="jd-sumtable"><tr><th colspan="12">Protected Constructors</th></tr>
<?cs call:write_method_summary(class.ctors.protected, 1) ?>
</table>
<?cs /if ?>

<?cs if:subcount(class.methods.public) ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- ========== METHOD SUMMARY =========== -->
<table id="pubmethods" class="jd-sumtable"><tr><th colspan="12">Public Methods</th></tr>
<?cs call:write_method_summary(class.methods.public, 1) ?>
</table>
<?cs /if ?>

<?cs if:subcount(class.methods.protected) ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- ========== METHOD SUMMARY =========== -->
<table id="promethods" class="jd-sumtable"><tr><th colspan="12">Protected Methods</th></tr>
<?cs call:write_method_summary(class.methods.protected, 1) ?>
</table>
<?cs /if ?>

<?cs # if there are inherited methods, write the table ?>
<?cs if:inhmethods ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- ========== METHOD SUMMARY =========== -->
<table id="inhmethods" class="jd-sumtable"><tr><th>
  <a href="#" class="toggle-all" onclick="return toggleAllInherited(this, null)">[Expand]</a>
  <div style="clear:left;">Inherited Methods</div></th></tr>
<?cs each:cl=class.inherited ?>
<?cs if:subcount(cl.methods) ?>
<tr class="api apilevel-<?cs var:cl.since.key ?>" >
<td colspan="12"><?cs call:expando_trigger("inherited-methods-"+cl.qualified, "closed") ?>
From <?cs var:cl.kind ?>
<?cs if:cl.included ?>
  <a href="<?cs var:toroot ?><?cs var:cl.link ?>"><?cs var:cl.qualified ?></a>
<?cs elif:cl.federated ?>
  <a href="<?cs var:cl.link ?>"><?cs var:cl.qualified ?></a>
<?cs else ?>
  <?cs var:cl.qualified ?>
<?cs /if ?>
<div id="inherited-methods-<?cs var:cl.qualified ?>">
  <div id="inherited-methods-<?cs var:cl.qualified ?>-list"
        class="jd-inheritedlinks">
  </div>
  <div id="inherited-methods-<?cs var:cl.qualified ?>-summary" style="display: none;">
    <table class="jd-sumtable-expando">
    <?cs call:write_method_summary(cl.methods, cl.included) ?></table>
  </div>
</div>
</td></tr>
<?cs /if ?>
<?cs /each ?>
</table>
<?cs /if ?>
<?cs /if ?>
</div><!-- jd-descr (summary) -->

<!-- Details -->

<?cs def:write_field_details(fields) ?>
<?cs each:field=fields ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<?cs # the A tag in the next line must remain where it is, so that Eclipse can parse the docs ?>
<a id="<?cs var:field.anchor ?>"></a>
<?cs # The apilevel-N class MUST BE LAST in the sequence of class names ?>
<div class="jd-details api apilevel-<?cs var:field.since.key ?>"> 
    <h4 class="jd-details-title">
      <span class="normal">
        <?cs var:field.scope ?> 
        <?cs var:field.static ?> 
        <?cs var:field.final ?> 
        <?cs call:type_link(field.type) ?>
      </span>
        <?cs var:field.name ?>
    </h4>
      <div class="api-level">
        <?cs call:since_tags(field) ?>
        <?cs call:federated_refs(field) ?>
      </div>
    <div class="jd-details-descr">
      <?cs call:description(field) ?>
    <?cs if:subcount(field.constantValue) ?>
        <div class="jd-tagdata">
        <span class="jd-tagtitle">Constant Value: </span>
        <span>
            <?cs if:field.constantValue.isString ?>
                <?cs var:field.constantValue.str ?>
            <?cs else ?>
                <?cs var:field.constantValue.dec ?>
                (<?cs var:field.constantValue.hex ?>)
            <?cs /if ?>
        </span>
        </div>
    <?cs /if ?>
    </div>
</div>
<?cs /each ?>
<?cs /def ?>

<?cs def:write_method_details(methods) ?>
<?cs each:method=methods ?>
<?cs # the A tag in the next line must remain where it is, so that Eclipse can parse the docs ?>
<a id="<?cs var:method.anchor ?>"></a>
<?cs # The apilevel-N class MUST BE LAST in the sequence of class names ?>
<div class="jd-details api apilevel-<?cs var:method.since.key ?>"> 
    <h4 class="jd-details-title">
      <span class="normal">
        <?cs var:method.scope ?> 
        <?cs var:method.static ?> 
        <?cs var:method.final ?> 
        <?cs var:method.abstract ?> 
        <?cs var:method.synchronized ?> 
        <?cs call:type_link(method.returnType) ?>
      </span>
      <span class="sympad"><?cs var:method.name ?></span>
      <span class="normal">(<?cs call:parameter_list(method.params) ?>)</span>
    </h4>
      <div class="api-level">
        <div><?cs call:since_tags(method) ?></div>
        <?cs call:federated_refs(method) ?>
      </div>
    <div class="jd-details-descr">
      <?cs call:description(method) ?>
    </div>
</div>
<?cs /each ?>
<?cs /def ?>

<?cs def:write_attr_details(attrs) ?>
<?cs each:attr=attrs ?>
<?cs # the A tag in the next line must remain where it is, so that Eclipse can parse the docs ?>
<a id="<?cs var:attr.anchor ?>"></a>
<?cs # The apilevel-N class MUST BE LAST in the sequence of class names ?>
<div class="jd-details api apilevel-<?cs var:attr.since.key ?>"> 
    <h4 class="jd-details-title"><?cs var:attr.name ?>
    </h4>
      <div class="api-level">
        <?cs call:since_tags(attr) ?>
      </div>
    <div class="jd-details-descr">
        <?cs call:description(attr) ?>

        <div class="jd-tagdata">
            <h5 class="jd-tagtitle">Related Methods</h5>
            <ul class="nolist">
            <?cs each:m=attr.methods ?>
                <li><a href="<?cs var:toroot ?><?cs var:m.href ?>"><?cs var:m.name ?></a></li>
            <?cs /each ?>
            </ul>
        </div>
    </div>
</div>
<?cs /each ?>
<?cs /def ?>


<!-- XML Attributes -->
<?cs if:subcount(class.attrs) ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- ========= FIELD DETAIL ======== -->
<h2>XML Attributes</h2>
<?cs call:write_attr_details(class.attrs) ?>
<?cs /if ?>

<!-- Enum Values -->
<?cs if:subcount(class.enumConstants) ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- ========= ENUM CONSTANTS DETAIL ======== -->
<h2>Enum Values</h2>
<?cs call:write_field_details(class.enumConstants) ?>
<?cs /if ?>

<!-- Constants -->
<?cs if:subcount(class.constants) ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- ========= ENUM CONSTANTS DETAIL ======== -->
<h2>Constants</h2>
<?cs call:write_field_details(class.constants) ?>
<?cs /if ?>

<!-- Fields -->
<?cs if:subcount(class.fields) ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- ========= FIELD DETAIL ======== -->
<h2>Fields</h2>
<?cs call:write_field_details(class.fields) ?>
<?cs /if ?>

<!-- Public ctors -->
<?cs if:subcount(class.ctors.public) ?>
<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- ========= CONSTRUCTOR DETAIL ======== -->
<h2>Public Constructors</h2>
<?cs call:write_method_details(class.ctors.public) ?>
<?cs /if ?>

<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- ========= CONSTRUCTOR DETAIL ======== -->
<!-- Protected ctors -->
<?cs if:subcount(class.ctors.protected) ?>
<h2>Protected Constructors</h2>
<?cs call:write_method_details(class.ctors.protected) ?>
<?cs /if ?>

<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- ========= METHOD DETAIL ======== -->
<!-- Public methdos -->
<?cs if:subcount(class.methods.public) ?>
<h2>Public Methods</h2>
<?cs call:write_method_details(class.methods.public) ?>
<?cs /if ?>

<?cs # this next line must be exactly like this to be parsed by eclipse ?>
<!-- ========= METHOD DETAIL ======== -->
<?cs if:subcount(class.methods.protected) ?>
<h2>Protected Methods</h2>
<?cs call:write_method_details(class.methods.protected) ?>
<?cs /if ?>

<?cs # the next two lines must be exactly like this to be parsed by eclipse ?>
<!-- ========= END OF CLASS DATA ========= -->
<a id="navbar_top"></a>

<?cs include:"footer.cs" ?>
</div> <!-- jd-content -->

</div><!-- end doc-content -->

<?cs include:"trailer.cs" ?>

</body>
</html>
