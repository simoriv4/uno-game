/**
 * classe che gestisce una Card che aggiunge 2 carte (color)
 */
public class CardAdd2Cards implements Card
{
    //attributi della CardAdd2Cards
    public String color;

    /**
     * costruttore parametrico della CardAdd2Cards
     * 
     * @param color color della Card
     */
    public CardAdd2Cards(String color)
    {
        //assegno i valori passati come parametro agli attributi della CardAdd2Cards
        this.color = color;
    }

    /**
     * costruttore non parametrico della CardAdd2Cards
     * 
     * crea una CardAdd2Cards "vuota"
     */
    public CardAdd2Cards()
    {
        //assegno valori di default agli attributi della CardAdd2Cards
        color = "";
    }

    @Override
    public void setColor(String color)
    {
        this.color = color;
    }

    @Override
    public String getColor()
    {
        return color;
    }

    @Override
    public void setNumber(int number)
    {
        //this.number = number;
    }

    @Override
    public int getNumber()
    {
        return -1;  //ritorno un valore di default perchè la CardCambiaGiro non ha un number
    }
    
    /**
     * metodo per confrontare il colore di quest carta con un colore
     * @param color colore con cui confrontare
     * @return true: colori uguali --- false: colori diversi
     */
    public boolean compareColor(String color)
    {
        return this.color.equals(color);
    }

    public boolean isPlayable(String color, int number)
    {
        return false;
    }
}