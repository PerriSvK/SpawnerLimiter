# SpawnerLimiter

Plugin na limitovanie pokladania spawnerov.

## Poziadavky

* Spigot 1.12
* Databáza

## Config.yml

```yaml
ip-based: true # Overovanie zalozene na IP
database: 
  host: localhost # server
  user: root # prihlasovacie meno
  pass: "123" # heslo
  db: "minecraft" # nazov databazy

groups: # skupiny
  default: 20 # nezmazatelne, pouziva sa ako zaloha
  vip: 25       # -+
  support: 30   # -+- Mozne zmazat, zmenit...
  admin: 35     # -+

msg:
  remaining: "&7Este mozes polozit &e&l%d &7spawnerov" # Vypise sa pri polozeni spawneru
  too-many: "&cDosiahol si limit spawnerov" # Vypise sa, ked ma hrac maximalny pocet spawnerov
  info: "&7Ako &e&l%s &7mozes polozit este &e&l%d &7spawnerov" # Vypise sa po prikaze
  info-other: "&7Hrac &e&l%s &7je skupina &e&l%s &7a moze polozit este &e&l%d &7spawnerov" # Vypise sa po prikaze
  no-perm: "&cNa tento prikaz nemas permissie"
```

## Prikazy

* `/spawnerlimiter` - Vypise pocet spawnerov, ktore mozem polozit
* `/spawnerlimiter <nick>` - Vypise pocet spawnerov, ktore moze polozit dany hrac

## Permissie

* `spawnerlimiter.group.<group>` - Permissia na danu skupinu z configu, zakladna je `default`
* `spawnerlimiter.cmd.me` - Povoluje prikaz na vypisanie, kolko spawnerov este mozem polozit, zakladne je to povolene
* `spawnerlimiter.cmd.other` - Povoluje prikaz na vypisanie, kolko spawnerov este moze dany hrac polozit
* `spawnerlimiter.bypass` - Povoluje preskocit kontrolu limitu