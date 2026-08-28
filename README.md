# VoucherPlugin

Plugin pro Paper/Spigot 1.21+, ktery vytvari "voucher papirky" - hrac je dostane
do inventare, klikne na ne pravym tlacitkem a dostane odmenu (rank, penize,
cokoliv jineho podle configu).

## Jak to funguje

- V `config.yml` si definujes libovolny pocet typu voucheru (sekce `vouchers:`).
- Kazdy typ ma svuj material (ikonu), jmeno, custom lore, volitelny glow efekt
  a seznam prikazu, ktere se spusti pres konzoli pri pouziti (napr. LuckPerms
  pro rank, EssentialsX/Vault ekonomiku pro penize - nebo cokoliv jineho).
- Voucher je oznacen v NBT datech itemu (PersistentDataContainer), takze hraci
  ho nemuzou podvrhnout/duplikovat pres creative mod.

## Build

Potrebujes Maven a JDK 21.

```
mvn clean package
```

Vysledny soubor najdes v `target/VoucherPlugin.jar` - nahraj ho do slozky
`plugins/` na serveru (Paper/Spigot 1.21+) a restartuj server.

## Prikazy

- `/voucher give <hrac> <voucher> [pocet]` - da hraci voucher (vyzaduje `voucher.admin`)
- `/voucher list` - vypise vsechny dostupne typy voucheru
- `/voucher reload` - znovu nacte config.yml bez restartu serveru

## Pridani noveho typu voucheru

Staci pridat novou sekci do `config.yml` pod `vouchers:`, napr.:

```yaml
vouchers:
  moj_novy_voucher:
    material: NETHER_STAR
    name: "&d&lSuper Voucher"
    lore:
      - "&7Klikni pravym tlacitkem!"
    glow: true
    consume-on-use: true
    commands:
      - "lp user %player% parent add mvp"
      - "eco give %player% 500"
    receive-message: "&aObdrzel jsi Super Voucher!"
    redeem-message: "&aZiskal jsi hodnost MVP a 500 Kc!"
    broadcast: "&d%player% ziskal MVP hodnost z voucheru!"
```

Pak stacem `/voucher reload` a novy typ je hned pouzitelny (`/voucher give hrac moj_novy_voucher`).

Placeholdery v `commands`, `receive-message`, `redeem-message` a `broadcast`:
- `%player%` - jmeno hrace
- `%uuid%` - UUID hrace
