<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title><?cs var:page.title ?></title>
    <style type="text/css">
    table {
        border-width: 1px 1px 1px 1px;
        border-spacing: 0px;
        border-style: solid solid solid solid;
        border-color: black black black black;
        border-collapse: collapse;
        background-color: white;
    }
    table th {
        border-width: 1px 1px 1px 1px;
        padding: 1px 4px 1px 3px;
        border-style: inset inset inset inset;
        border-color: gray gray gray gray;
        background-color: white;
    }
    table td {
        border-width: 1px 1px 1px 1px;
        padding: 1px 4px 1px 3px;
        border-style: inset inset inset inset;
        border-color: gray gray gray gray;
        background-color: white;
    }
    </style>
</head>
<body>
<h1><?cs var:page.title ?></h1>

<h2>Overall</h2>
<table>
<tr><th>Errors</th><td><?cs var:all.errorCount ?></td></tr>
<tr><th>Percent Good</th><td><?cs var:all.percentGood ?></td></tr>
<tr><th>Total Comments</th><td><?cs var:all.totalCount ?></td></tr>
</table>

<h2>Package Summary</h2>

<table>
<tr>
    <th>Package</th>
    <th>Errors</th>
    <th>Percent Good</th>
    <th>Total</th>
</tr>
<?cs each:pkg=packages ?>
<tr>
    <td><?cs var:pkg.name ?></td>
    <td><?cs var:pkg.errorCount ?></td>
    <td><?cs var:pkg.percentGood ?></td>
    <td><?cs var:pkg.totalCount ?></td>
</tr>
<?cs /each ?>
</table>


<h2>Class Summary</h3>

<table>
<tr>
    <th>Class</th>
    <th>Errors</th>
    <th>Percent Good</th>
    <th>Total</th>
</tr>
<?cs each:cl=classes ?>
<tr>
    <td><a href="#class_<?cs var:cl.qualified ?>"><?cs var:cl.qualified ?></a></td>
    <td><?cs var:cl.errorCount ?></td>
    <td><?cs var:cl.percentGood ?></td>
    <td><?cs var:cl.totalCount ?></td>
</tr>
<?cs /each ?>
</table>

<h2>Detail</h2>

<?cs each:cl=classes ?>
<h3><a id="class_<?cs var:cl.qualified ?>"><?cs var:cl.qualified ?></a></h3>
<p>Errors: <?cs var:cl.errorCount ?><br/>
Total: <?cs var:cl.totalCount ?><br/>
Percent Good: <?cs var:cl.percentGood ?></p>
<table>
<?cs each:err=cl.errors ?>
<tr>
    <td><?cs var:err.pos ?></td>
    <td><?cs var:err.name ?></td>
    <td><?cs var:err.descr ?></td>
</tr>
<?cs /each ?>
</table>

<?cs /each ?>

</body>
</html>
