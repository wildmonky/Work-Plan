import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_38c2560c8719027648d686f0b6d284d6
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String pdname = mapRow.get("ID");
String url="javascript:window.open('dataadd.html?dataname=OMS_JZB_EDIT&&actiontype=+"pdname"+&pk_id="+mapRow.get("ID")+"');";

    }
}
