import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_60187a03008d23fcd64f32b2bf48c749
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String sResult=mapRow.get("OTIME").toString();
String isElink=mapRow.get("ISELINK").toString();
String isCall=mapRow.get("ISCALL").toString();
sResult=("1".equals(isElink)) || ("1".equals(isCall))?sResult:"";
return sResult;
    }
}
