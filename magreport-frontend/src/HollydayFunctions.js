
export default function isHollyday(){
    let newDate = new Date()
    let date = newDate.getDate();
    let month = newDate.getMonth() + 1;
    if ((month === 12 && date>9) || (month === 1 && date <15))
        {
            if (date%2 === 0) return 0
            if (date%5 === 0) return 1
            return 2
        }
    if (month === 3 && date<11)
    {
        return 3
    }
         
    return -1
}