<?php
use Ratchet\Server\IoServer;
use Ratchet\WebSocket\WsServer;
use Horserider\Movimientos;

    require dirname(__DIR__) . '/vendor/autoload.php';

    $server = IoServer::factory(
        new WsServer(new Movimientos())
      , 8880
    );

    $server->run();