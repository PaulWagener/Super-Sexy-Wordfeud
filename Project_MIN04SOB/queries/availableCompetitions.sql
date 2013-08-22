SELECT `competitie_id` 
FROM   `deelnemer` AS `d` 
       JOIN `competitie` AS `c` 
         ON `c`.`id` = `d`.`competitie_id` 
WHERE  `competitie_id` NOT IN (SELECT `competitie_id` 
                               FROM   `deelnemer` 
                               WHERE  `account_naam` = ?) 
       AND DATEDIFF(NOW(), `einde`) < 0 
GROUP  BY `competitie_id` 
