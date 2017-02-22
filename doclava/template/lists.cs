var DATA = [
<?cs each:page = docs.pages
?>      { id:<?cs var: page.id ?>, label:"<?cs var:page.label ?>", link:"<?cs var:page.link ?>", type:"<?cs var:page.type ?>" }<?cs if:!last(page) ?>,<?cs /if ?>
<?cs /each ?>
    ];
