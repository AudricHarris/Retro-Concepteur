<?php

require_once '../app/entities/Responsable.php';

class Matiere
{
    public function __construct (
		private ?int $id,
		private string $code,
		private string $nom,
		private int $heuresTD,
		private int $heuresTP,
		private ?Responsable $responsable,
		private int $semestre
	){}
	

	public function getId():?int
    {
        return $this->id;
    }

	public function getCode():string
    {
        return $this->code;
    }

	public function getNom():string
    {
        return $this->nom;
    }

	public function getHeureTD():int
    {
        return $this->heureTD;
    }

	public function getHeureTP():int
    {
        return $this->heureTP;
    }

	public function getResponsable():?Responsable
    {
        return $this->responsable;
    }

	public function getSemestre():int
    {
        return $this->semestre;
    }

	public function setId(int $id):int
	{
		$this-> id = $id;
	}

}