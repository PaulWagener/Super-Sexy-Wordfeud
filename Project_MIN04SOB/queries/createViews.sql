-- phpMyAdmin SQL Dump
-- version 3.5.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Aug 23, 2013 at 10:44 AM
-- Server version: 5.5.29
-- PHP Version: 5.3.13

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `tjmbrouw_db2`
--

-- --------------------------------------------------------

--
-- Stand-in structure for view `comp_ranking`
--
CREATE TABLE IF NOT EXISTS `comp_ranking` (
`Competitie_id` int(11)
,`account_naam` varchar(25)
,`wins` int(0)
,`los` int(0)
,`this_num_games` bigint(21)
,`avgscore` decimal(36,4)
,`totalscore` decimal(54,0)
,`bayesian_rating` decimal(47,12)
);
-- --------------------------------------------------------

--
-- Stand-in structure for view `rank_comp_score`
--
CREATE TABLE IF NOT EXISTS `rank_comp_score` (
`avgscore` decimal(36,4)
,`totalscore` decimal(54,0)
,`account_naam` varchar(25)
,`Competitie_ID` int(11)
);
-- --------------------------------------------------------

--
-- Stand-in structure for view `rank_gamescore`
--
CREATE TABLE IF NOT EXISTS `rank_gamescore` (
`gamescore` decimal(32,0)
,`account_naam` varchar(25)
,`spel_id` int(11)
,`Competitie_ID` int(11)
);
-- --------------------------------------------------------

--
-- Stand-in structure for view `rank_winlos`
--
CREATE TABLE IF NOT EXISTS `rank_winlos` (
`Competitie_ID` int(11)
,`account_naam` varchar(25)
,`wins` int(0)
,`los` int(0)
);
-- --------------------------------------------------------

--
-- Structure for view `comp_ranking`
--
DROP TABLE IF EXISTS `comp_ranking`;

CREATE ALGORITHM=UNDEFINED DEFINER=`tjmbrouw`@`%` SQL SECURITY DEFINER VIEW `comp_ranking` AS select `rank_winlos`.`Competitie_ID` AS `Competitie_id`,`rank_winlos`.`account_naam` AS `account_naam`,`rank_winlos`.`wins` AS `wins`,`rank_winlos`.`los` AS `los`,`rank_bayesian`.`this_num_games` AS `this_num_games`,`rank_comp_score`.`avgscore` AS `avgscore`,`rank_comp_score`.`totalscore` AS `totalscore`,`ranking`.`bayesian_rating` AS `bayesian_rating` from (((`rank_winlos` join `rank_bayesian` on(((`rank_bayesian`.`Competitie_ID` = `rank_winlos`.`Competitie_ID`) and (`rank_winlos`.`account_naam` = `rank_bayesian`.`account_naam`)))) join `rank_comp_score` on(((`rank_winlos`.`Competitie_ID` = `rank_comp_score`.`Competitie_ID`) and (`rank_winlos`.`account_naam` = `rank_comp_score`.`account_naam`)))) join `ranking` on(((`rank_winlos`.`Competitie_ID` = `ranking`.`Competitie_ID`) and (`rank_winlos`.`account_naam` = `ranking`.`account_naam`))));

-- --------------------------------------------------------

--
-- Structure for view `rank_comp_score`
--
DROP TABLE IF EXISTS `rank_comp_score`;

CREATE ALGORITHM=UNDEFINED DEFINER=`tjmbrouw`@`%` SQL SECURITY DEFINER VIEW `rank_comp_score` AS select avg(`rank_gamescore`.`gamescore`) AS `avgscore`,sum(`rank_gamescore`.`gamescore`) AS `totalscore`,`rank_gamescore`.`account_naam` AS `account_naam`,`rank_gamescore`.`Competitie_ID` AS `Competitie_ID` from `rank_gamescore` group by `rank_gamescore`.`Competitie_ID`,`rank_gamescore`.`account_naam`;

-- --------------------------------------------------------

--
-- Structure for view `rank_gamescore`
--
DROP TABLE IF EXISTS `rank_gamescore`;

CREATE ALGORITHM=UNDEFINED DEFINER=`tjmbrouw`@`%` SQL SECURITY DEFINER VIEW `rank_gamescore` AS select sum(`beurt`.`score`) AS `gamescore`,`beurt`.`Account_naam` AS `account_naam`,`beurt`.`Spel_ID` AS `spel_id`,`spel`.`Competitie_ID` AS `Competitie_ID` from (`beurt` join `spel` on((`spel`.`ID` = `beurt`.`Spel_ID`))) group by `beurt`.`Spel_ID`,`beurt`.`Account_naam`;

-- --------------------------------------------------------

--
-- Structure for view `rank_winlos`
--
DROP TABLE IF EXISTS `rank_winlos`;

CREATE ALGORITHM=UNDEFINED DEFINER=`tjmbrouw`@`%` SQL SECURITY DEFINER VIEW `rank_winlos` AS select `sp`.`Competitie_ID` AS `Competitie_ID`,`sc`.`Account_naam` AS `account_naam`,(case when (`wsc`.`winnerscore` = `sc`.`totaalscore`) then 1 else 0 end) AS `wins`,(case when (`wsc`.`winnerscore` = `sc`.`totaalscore`) then 0 else 1 end) AS `los` from ((`score` `sc` join `rank_winnerscore` `wsc` on((`sc`.`Spel_ID` = `wsc`.`Spel_ID`))) join `spel` `sp` on((`sc`.`Spel_ID` = `sp`.`ID`)));

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
