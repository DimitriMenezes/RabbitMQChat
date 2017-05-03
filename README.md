# RabbitMQChat
A Android chat app using RabbitMQ to store messages


SETTING A INSTANCE OF RABBITMQ NODE:

/*Step 1: Installing the apt, adding user and permissions
sudo apt-get install erlang
sudo apt-get update
sudo apt-get install rabbitmq-server
sudo rabbitmq-plugins enable rabbitmq_management
sudo rabbitmqctl add_user admin admin
sudo rabbitmqctl set_permissions admin ".*" ".*" ".*" 
sudo rabbitmqctl authenticate_user admin admin
sudo rabbitmqctl set_user_tags admin administrator
sudo rabbitmqctl add_vhost admin
sudo rabbitmqctl set_permissions -p admin admin ".*" ".*" ".*" 

/*Optional */
sudo rabbitmqctl delete_vhost /
sudo rabbitmqctl delete_user guest

/* Step 2: Just restart this Node*/
sudo /etc/init.d/rabbitmq-server restart
 
/* Step 3: Make sure all instances have the same cookie
 It's a code like:  MIZIIEKTBIOHKBUTTGFN
*/

// To edit the cookie
sudo nano /var/lib/rabbitmq/.erlang.cookie

//Restart in all nodes
sudo /etc/init.d/rabbitmq-server restart

/* Step 4: Adding one or many instances to a cluster */
sudo rabbitmqctl stop_app
sudo rabbitmqctl join_cluster rabbit@ip-MASTER
sudo rabbitmqctl start_app
sudo rabbitmqctl cluster_status

///Step 5: Setting POLICE HA
rabbitmqctl set_policy -p admin HA  '^(?!amq\.).*' '{"ha-mode": "all"}'
