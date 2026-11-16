import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_49d543ab7b3019fab1ea74d5a438cb48
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String sResult=mapRow.get("ISELINK").toString();
sResult="1".equals(sResult)?"已发文字通知":("0".equals(sResult)?"未发文字通知":sResult);
return sResult;
    }
}
