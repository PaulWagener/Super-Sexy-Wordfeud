SELECT `gl`.`spel_id` 
       , `gl`.`beurt_id` 
       , `l`.`lettertype_karakter` 
       , `gl`.`tegel_x` 
       , `gl`.`tegel_y` 
       , `gl`.`blancoletterkarakter` 
       , `l`.`id` 
       , `lt`.`waarde` 
  FROM `gelegdeletter` AS `gl` 
       JOIN `letter` AS `l` 
         ON `l`.`spel_id` = `gl`.`spel_id` 
            AND `l`.`id` = `gl`.`letter_id` 
       JOIN `spel` `s` 
         ON `s`.`id` = `gl`.`spel_id` 
       JOIN `letterset` AS `ls` 
         ON `ls`.`code` = `s`.`letterset_naam` 
       JOIN `lettertype` AS `lt` 
         ON `ls`.`code` = `lt`.`letterset_code` 
            AND `l`.`lettertype_karakter` = `lt`.`karakter` 
 WHERE `gl`.`spel_id` = ? AND `gl`.`beurt_id` <= ?
