<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<?cs if:project.name ?>
  <meta name="description" content="Javadoc API documentation for <?cs var:project.name ?>." />
<?cs else ?>
  <meta name="description" content="Javadoc API documentation." />
<?cs /if ?>
<link rel="shortcut icon" type="image/x-icon" href="<?cs var:toroot ?>favicon.ico" />
<title>
<?cs if:page.title ?>
  <?cs var:page.title ?>
<?cs /if ?>
<?cs if:project.name ?>
| <?cs var:project.name ?>
<?cs /if ?>
</title>
<link href="<?cs var:toassets ?>doclava-developer-docs.css" rel="stylesheet" type="text/css" />
<link href="<?cs var:toassets ?>customizations.css" rel="stylesheet" type="text/css" />
<script src="<?cs var:toassets ?>search_autocomplete.js" type="text/javascript"></script>
<script src="<?cs var:toassets ?>jquery-resizable.min.js" type="text/javascript"></script>
<script src="<?cs var:toassets ?>doclava-developer-docs.js" type="text/javascript"></script>
<script src="<?cs var:toassets ?>prettify.js" type="text/javascript"></script>
<script type="text/javascript">
  setToRoot("<?cs var:toroot ?>", "<?cs var:toassets ?>");
</script><?cs 
if:reference ?>
<script src="<?cs var:toassets ?>doclava-developer-reference.js" type="text/javascript"></script>
<script src="<?cs var:toassets ?>navtree_data.js" type="text/javascript"></script><?cs 
/if ?>
<script src="<?cs var:toassets ?>customizations.js" type="text/javascript"></script>
<noscript>
  <style type="text/css">
    html,body{overflow:auto;}
    #body-content{position:relative; top:0;}
    #doc-content{overflow:visible;border-left:3px solid #666;}
    #side-nav{padding:0;}
    #side-nav .toggle-list ul {display:block;}
    #resize-packages-nav{border-bottom:3px solid #666;}
  </style>
</noscript>
</head>
